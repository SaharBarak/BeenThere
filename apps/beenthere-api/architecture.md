# BeenThere API — Architecture

> **Scope**: Backend service only. Kotlin + Spring Boot **WebFlux** with **R2DBC** and PostgreSQL. This doc is the single source of truth for the API’s design, invariants, and operational rules.

---

## 1) Overview

The BeenThere API provides endpoints for:

* **Discovery**: listing feeds for entire places or roommate constellations.
* **Swiping & Matching**: LIKE/PASS on users or listings; create matches on mutual interest (or auto‑accept).
* **Messaging**: chat per match (MVP minimal).
* **Ratings**: house, roommate, landlord.
* **Subscriptions**: gate swipes/messages to active subscribers; webhooks flip status.

**Non‑Goals (MVP)**: payments provider integration (use fake gateway), full text search, map tiles, push notifications.

---

## 2) Tech Stack

* **Runtime**: Kotlin 2.x, Java 21, Spring Boot 3.3.x
* **Web**: Spring **WebFlux** (Netty), Validation, Security, Actuator
* **Data**: Spring Data **R2DBC** (PostgreSQL reactive driver). JDBC present only for Flyway.
* **Migrations**: **Flyway** (append‑only), `db/migration`.
* **Serialization**: Jackson + **jackson-module-kotlin**.
* **Observability**: Micrometer + Prometheus registry.
* **Lint/Quality**: detekt + ktlint; JUnit 5 + Testcontainers for DB tests.

**Hard Invariants**

* No JPA, no Spring MVC, no blocking DB calls on request path.
* Cursor (seek) pagination only; **no OFFSET**.
* IDs = UUID, timestamps = `OffsetDateTime` UTC.
* JSONB stored as `text` in entities, parsed/validated at DTO layer.

---

## 3) Package Layout

```
com.beenthere
  ├─ ApiApplication.kt
  ├─ api/              # REST controllers + DTOs only
  ├─ auth/             # (MVP+) JWT, filters, security config
  ├─ common/           # enums, error types, cursor codecs, utils
  ├─ listing/          # Listing entities/repos/queries
  ├─ swipe/            # Swipe entities/repo/service
  ├─ match/            # Match + Message entities/repos/services
  ├─ rating/           # House/Roommate/Landlord ratings
  ├─ subscription/     # Subscription entity/repo, billing webhook
  └─ config/           # ObjectMapper, CORS, Security, R2DBC config (if needed)
```

---

## 4) Data Model

Tables (via Flyway):

* **users**(id, email, password\_hash, role, created\_at)
* **profiles**(user\_id PK→users.id, display\_name, age, bio, budget\_min, budget\_max, city, prefs jsonb)
* **listings**(id, owner\_user\_id→users.id, type\['ROOMMATE\_GROUP'|'ENTIRE\_PLACE'], title, city, price, attrs jsonb, auto\_accept bool, is\_active bool, created\_at)
* **listing\_photos**(id, listing\_id→listings.id, url, sort)
* **subscriptions**(id, user\_id unique→users.id, status\['NONE'|'ACTIVE'|'EXPIRED'|'CANCELED'], period\_start, period\_end, provider, provider\_ref, created\_at)
* **swipes**(id, user\_id→users.id, target\_type\['USER'|'LISTING'], target\_id, action\['LIKE'|'PASS'], created\_at, **unique(user\_id,target\_type,target\_id)**)
* **matches**(id, kind\['USER\_USER'|'USER\_LISTING'], a\_user\_id→users.id, b\_subject\_type\['USER'|'LISTING'], b\_subject\_id, created\_at, **unique(a\_user\_id,b\_subject\_type,b\_subject\_id)**)
* **messages**(id, match\_id→matches.id, sender\_user\_id→users.id, body, created\_at)
* **ratings\_house**(id, rater\_user\_id→users.id, listing\_id?→listings.id, address\_hash?, scores jsonb, comment?, created\_at)
* **ratings\_roommate**(id, rater\_user\_id→users.id, ratee\_user\_id→users.id, scores jsonb, comment?, created\_at)
* **ratings\_landlord**(id, rater\_user\_id→users.id, landlord\_user\_id→users.id, scores jsonb, comment?, created\_at)

**Indexes** (indicative):

* listings(owner\_user\_id, is\_active), listings(city, price)
* swipes(user\_id, created\_at DESC)
* matches(a\_user\_id, created\_at DESC)
* messages(match\_id, created\_at)
* ratings\_\* on subject ids

**ENUM Strategy**: use TEXT + CHECK constraints in SQL for compatibility with R2DBC + migrations.

**Privacy**: store `address_hash` (normalized address → SHA‑256) to avoid storing exact addresses for current‑home ratings.

---

## 5) Core Flows

### 5.1 Discovery (Feed)

* **Input**: `city`, `minBudget`, `maxBudget`, `cursor?`, `limit<=50`.
* **Filter**: listings is\_active=true, city match, price range, exclude already swiped by viewer.
* **Order**: `created_at DESC, id DESC`.
* **Pagination**: return `limit+1`; if over, drop last and encode `nextCursor = base64("epochSeconds|uuid")`.
* (MVP+) Incorporate trust score ranking later.

### 5.2 Swipe → Match

1. `POST /swipes` with `{targetType, targetId, action}`
2. Check **subscription ACTIVE**.
3. Upsert into `swipes` (on conflict update action + timestamp).
4. If `action=LIKE` then:

    * **USER↔USER**: match if target has LIKEd actor.
    * **USER↔LISTING**: match if listing.auto\_accept, else require owner LIKEd actor.
5. Insert into `matches` using `ON CONFLICT DO NOTHING` + return existing if duplicate.

### 5.3 Messaging

* One chat per match. Basic text messages.
* Rate limiting (MVP+): per user/match sliding window.

### 5.4 Ratings

* 3 create endpoints. Validate `scores` via schema (zod‑like on mobile; Bean Validation server‑side if needed).
* Aggregation done offline (MVP+ materialized view or scheduled job).

### 5.5 Subscriptions

* `GET /billing/status` (reads `subscriptions`).
* `POST /billing/webhook` flips `status`/period.
* Gates: `SwipeService` & `MessageService` call `ensureActive(userId)`.

---

## 6) HTTP API (MVP)

Base path: `/api/v1`

**Listings**

* `GET /listings/feed?city=&minBudget=&maxBudget=&cursor=&limit=` → `{ items: ListingCardDTO[], nextCursor? }`
* `POST /listings` (MVP+) create listing for owners.

**Swipes & Matches**

* `POST /swipes` → `SwipeResponse{ matchId? }`
* `GET /matches` (MVP+) → list of matches with last message/unread.

**Messages**

* `GET /matches/{id}/messages?cursor=&limit=`
* `POST /matches/{id}/messages` → create.

**Ratings**

* `POST /ratings/house` | `/ratings/roommate` | `/ratings/landlord`

**Billing**

* `GET /billing/status`
* `POST /billing/webhook` (provider → server)

**Actuator**

* `/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`

---

## 7) DTOs & Serialization

* **DTOs** in `api/dto/` mirror HTTP payloads (no entities leaked).
* **Casing**: JSON keys `camelCase`.
* **Time**: ISO‑8601 with timezone (OffsetDateTime).
* **Cursor**: base64 URL‑safe string `"epochSeconds|uuid"`.

---

## 8) Error Handling

* Use a sealed error model or `Result` types inside services.
* Map to HTTP via `@ControllerAdvice`:

    * 400 validation errors
    * 401 unauthenticated (MVP placeholder)
    * 403 subscription not active
    * 404 resource not found
    * 409 conflict (duplicate, business rule)
    * 429 rate limit (MVP+)

---

## 9) Security

* **MVP**: temporary `currentUserId()` in controllers for dev.
* **MLP**: JWT auth (access + refresh). Spring Security config:

    * Permit `/auth/**`, `/billing/webhook`, `/actuator/health`.
    * Auth required for all other endpoints.
* CORS: allow mobile dev origins.
* Store password hashes using Argon2/BCrypt (MVP: placeholder ok). Never log secrets.

---

## 10) Persistence & Queries

* Repositories are **Coroutine** interfaces (R2DBC).
* Custom `@Query` for feed seek pagination and swipe/match checks.
* All write paths are **idempotent** using DB uniqueness constraints.

---

## 11) Configuration

`application.yml` keys:

```yaml
spring:
  r2dbc: { url, username, password }
  datasource: { url, username, password }  # for Flyway only
  flyway: { enabled: true, locations: classpath:db/migration }
management:
  endpoints:
    web:
      exposure: { include: health,info,metrics,prometheus }
server: { port: 8080 }
```

Env override via `SPRING_APPLICATION_JSON` or profile‑specific `application-local.yml`.

---

## 12) Testing Strategy

* **Unit**: services pure logic (swipe → match matrix; cursor math).
* **Integration**: repository queries with **Testcontainers** Postgres.
* **Contract**: (MVP+) Spring WebTestClient tests for endpoints (golden payloads).
* Tests must run headless and seed data via Flyway + setup SQL.

---

## 13) Observability

* Expose Prometheus metrics at `/actuator/prometheus`.
* Standard JVM/Netty metrics; app counters (MVP+): swipes, matches created, messages sent.
* Structured logging (JSON) when deployed; console in dev.

---

## 14) Deployment

* Build JAR with Spring Boot plugin; containerize with Temurin JRE base.
* Health probes: `/actuator/health` (readiness/liveness split in MLP).
* Externalize DB/secret config via env.

**Dockerfile (indicative)**

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/beenthere-api-*.jar app.jar
ENV JAVA_OPTS="-XX:+ExitOnOutOfMemoryError"
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
```

---

## 15) Change Management

* Flyway is **append‑only**.
* Public DTOs require **compat review** (mobile depends on them).
* All PRs must pass: detekt, ktlint, unit/integration tests.
* Keep diffs focused; no cross‑cutting refactors without plan.

---

## 16) Roadmap (API)

* **Near‑term**: JWT auth, create/listing endpoints, owner‑like check for user→listing matches, message rate limits, rating read aggregates.
* **Mid‑term**: OpenAPI generation, S3/MinIO media upload signing, trust‑score materialized views, search filters.
* **Longer‑term**: Feature flags, per‑tenant throttles, outbox pattern for async events.
