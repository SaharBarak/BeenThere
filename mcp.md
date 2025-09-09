# BeenThere — Model Context (Workspace Rules for Humans + AI)

> **Purpose**: This document tells humans and coding assistants exactly how to work in this repo. It encodes architecture decisions, allowed libraries, invariants, coding standards, and the change‑management protocol. Treat it as the *single source of truth* for how BeenThere is built.

---

## 0) TL;DR (Non‑negotiables)

* **Monorepo** with `apps/mobile` (Expo RN, TypeScript) and `apps/api` (Kotlin, Spring Boot WebFlux + R2DBC).
* **PostgreSQL + Flyway** for schema; **never edit past migrations**. Add a new `V{N}__*.sql` for changes.
* **Reactive only**: WebFlux + R2DBC. **Do not add JPA** or Spring Web MVC. **No blocking DB calls**.
* **Seek pagination only** (cursor), no `OFFSET`.
* **Auth & access**: JWT (soon), subscription gate on swipes/messages.
* **Small, test‑backed PRs** only. Lint and tests must pass.

---

## 1) Repo Layout

```
beenthere/
  apps/
    mobile/         # Expo React Native (TypeScript)
    api/            # Spring Boot (Kotlin, WebFlux, R2DBC)
  packages/
    ui/             # (optional) shared RN UI kit
    tsconfig/
    eslint-config/
  infra/
    docker/docker-compose.yml   # Postgres (+ optional pgAdmin)
  .github/workflows/ci.yml      # CI (gradle + pnpm)
  turbo.json
  pnpm-workspace.yaml
  README.md
  ARCHITECTURE.md (this file can be kept in sync with Model Context)
```

---

## 2) Versions & Tooling

* **Java**: 21 (LTS)
* **Kotlin**: 2.0.x
* **Spring Boot**: 3.3.x (stable)
* **DB**: PostgreSQL 16
* **Package managers**: Gradle (Kotlin/Groovy DSL) for API; pnpm for JS.
* **Mobile**: Expo SDK latest stable, React Native, TypeScript (strict).

---

## 3) Backend (API) Architecture

### 3.1 Tech Stack

* Spring Boot: **WebFlux**, **Security**, **Validation**, **Actuator**
* Data: **Spring Data R2DBC**, PostgreSQL drivers (Reactive + JDBC for Flyway)
* Migrations: **Flyway**
* JSON: **Jackson Kotlin**
* Observability: **Micrometer + Prometheus** (registry at runtime)
* Lint: **detekt** + **ktlint** (CI‑blocking)
* Optional functional helpers: `io.github.michaelbull:kotlin-result:2.x`

### 3.2 Project Invariants (Hard Rules)

* **No JPA**, **No Spring Web MVC**. Do not add these dependencies.
* Do not call blocking JDBC from request paths. JDBC is present **only** so Flyway can run.
* **UUID** for primary keys, **OffsetDateTime** for timestamps (UTC). JSONB stored as `text` mapped fields in entities.
* **Cursor (seek) pagination** everywhere: `(created_at, id)` pair → base64 cursor `"ts|uuid"`.
* **Unique constraints** enforce idempotency for swipes and matches.

### 3.3 Packages (API)

```
com.beenthere
  ├─ auth/            # (soon) JWT, filters, guards
  ├─ common/          # enums, error types, cursor codecs
  ├─ user/            # User, Profile
  ├─ listing/         # Listing, ListingPhoto, feed queries
  ├─ swipe/           # Swipe upsert + queries
  ├─ match/           # Match & Message
  ├─ rating/          # Ratings (house, roommate, landlord)
  ├─ subscription/    # Subscription (status + period)
  └─ api/             # Controllers + DTOs only
```

### 3.4 Data Model (MVP)

**Tables** (created via Flyway):

* `users(id, email, password_hash, role, created_at)`
* `profiles(user_id, display_name, age, bio, budget_min, budget_max, city, prefs jsonb)`
* `listings(id, owner_user_id, type['ROOMMATE_GROUP'|'ENTIRE_PLACE'], title, city, price, attrs jsonb, auto_accept, is_active, created_at)`
* `listing_photos(id, listing_id, url, sort)`
* `subscriptions(id, user_id unique, status['NONE'|'ACTIVE'|'EXPIRED'|'CANCELED'], period_start, period_end, provider, provider_ref, created_at)`
* `swipes(id, user_id, target_type['USER'|'LISTING'], target_id, action['LIKE'|'PASS'], created_at, unique (user_id,target_type,target_id))`
* `matches(id, kind['USER_USER'|'USER_LISTING'], a_user_id, b_subject_type['USER'|'LISTING'], b_subject_id, created_at, unique (a_user_id,b_subject_type,b_subject_id))`
* `messages(id, match_id, sender_user_id, body, created_at)`
* `ratings_house(id, rater_user_id, listing_id?, address_hash?, scores jsonb, comment?, created_at)`
* `ratings_roommate(id, rater_user_id, ratee_user_id, scores jsonb, comment?, created_at)`
* `ratings_landlord(id, rater_user_id, landlord_user_id, scores jsonb, comment?, created_at)`

**Indexes**: see migrations (feed + auditing + lookups). No `OFFSET` pagination.

### 3.5 Key Flows

**Swipe → Match**

* Upsert swipe with `ON CONFLICT DO UPDATE` (idempotent).
* If `LIKE`:

    * `USER↔USER`: match only if the other user already `LIKE`d you.
    * `USER↔LISTING`: match if `listings.auto_accept=true`, else require owner has `LIKE`d the user.
* Create match inside one logical operation using unique constraint to prevent duplicates.

**Feed**

* `GET /api/v1/listings/feed?city=&minBudget=&maxBudget=&cursor=`
* Filter: active listing, not already swiped, budget overlap, same city.
* Order: `created_at DESC, id DESC`. Return `limit+1` to compute `nextCursor`.

**Subscriptions**

* Only **ACTIVE** subscribers can swipe/message (gate in services).
* Billing webhooks flip `subscriptions.status`.

### 3.6 API Surface (MVP)

* `GET  /api/v1/listings/feed` → feed (seek pagination)
* `POST /api/v1/swipes` → `{ targetType, targetId, action }` → `{ matchId? }`
* **Soon**: `GET /api/v1/matches`, `GET/POST /api/v1/matches/{id}/messages`
* **Soon**: `POST /api/v1/ratings/{house|roommate|landlord}`
* **Auth** placeholders until JWT is wired; temporary `currentUserId()` in controllers for dev only.

### 3.7 Code Style (API)

* Kotlin data classes, sealed types for domain.
* Controllers: thin (DTO in/out). Services: orchestration & invariants. Repos: SQL only.
* Error handling: prefer `Result`/sealed error types; map to HTTP in a single `@ControllerAdvice`.
* Coroutines: all repo/service APIs are `suspend` or return `Flow<>`.
* JSONB fields modeled as `String` in entities, parsed at edges.

### 3.8 Migrations Policy

* **Never edit old migrations.** Always add `V{N}__description.sql`.
* If a rollback is needed, add a forward migration that fixes state; document in PR.

### 3.9 Security & Privacy

* Keep exact addresses private; use `address_hash` for current-home ratings.
* Post‑match reveal for sensitive details.
* Add `rate limiting` (Redis) later; placeholder guards in services now.
* Actuator exposure limited to `health,info,metrics,prometheus`.

---

## 4) Mobile (Expo RN) Architecture

### 4.1 Tech Stack

* **Expo + React Native (TypeScript, strict)**
* State: **Zustand**
* Data fetching: **TanStack Query**
* Validation: **Zod**
* Navigation: **React Navigation**
* HTTP: **Axios** (typed wrappers)

### 4.2 Folder Layout (mobile)

```
apps/mobile/src/
  app/                 # screens (tabs): Discover, Chat, Profile, Paywall
  components/          # ListingCard, SwipeDeck, TrustBadge
  lib/                 # api.ts, query.ts, env.ts, storage.ts
  store/               # auth.ts, subscription.ts
  validators/          # zod schemas
  hooks/               # useFeed, useSwipe
  types/               # DTOs matching API
```

### 4.3 UI/UX Rules

* Tinder‑style 3‑card deck (Reanimated + Gesture Handler).
* Paywall gate when subscription inactive; allow limited preview swipes.
* Map view (later), filters (budget, city, pets, smoking).

### 4.4 API Contracts (mobile)

* Align DTOs 1:1 with server. Cursor build steps should **not** change API shapes.
* Cursor: if server DTOs change, update `types/` and validators.

### 4.5 Quality Gates (mobile)

* ESLint strict, TypeScript `strict: true`.
* Unit tests for pure logic; e2e later.

---

## 5) Dev, Build, and Run

### 5.1 Infra (DB)

* Compose file at `infra/docker/docker-compose.yml` runs Postgres (and optional pgAdmin).
* Local DB connection:

    * R2DBC: `r2dbc:postgresql://localhost:5432/beenthere`
    * JDBC (Flyway): `jdbc:postgresql://localhost:5432/beenthere`
    * Username/password: `postgres/postgres`

### 5.2 Commands

**API**

```
cd apps/api
./gradlew detekt ktlintCheck test build
./gradlew bootRun
```

**Mobile**

```
cd apps/mobile
pnpm install
pnpm expo start
```

**Monorepo** (Turbo example)

```
pnpm dev        # starts both once wired
pnpm -w lint    # run linters across workspace
```

---

## 6) CI / PR Policy

* All PRs must:

    * Pass **detekt + ktlint + tests** (API)
    * Pass **eslint + typecheck** (mobile)
    * Not alter old Flyway migrations
    * Keep diffs small (ideally ≤ 200 LOC) and single‑purpose
* CI: GitHub Actions runs API & Mobile jobs in parallel.

---

## 7) AI Collaboration Protocol (Cursor/Claude/GPT)

**What the model MAY edit**

* API: controllers, services, repos, DTOs, *new* Flyway migrations, tests, configs.
* Mobile: screens, components, hooks, types, validators.

**What the model MUST NOT do**

* Add JPA / Spring Web MVC / blocking JDBC to request paths.
* Edit or delete existing Flyway files.
* Break public API shapes without explicit instruction.
* Introduce `OFFSET` pagination.

**Change workflow**

1. If addressing a bug/feature, first produce a short plan and impacted files list.
2. Add/adjust tests → see them fail → implement fix → turn tests green.
3. Keep architectural invariants; if a change needs an invariant break, propose it in comments first.

**Prompts to use**

* *Audit*: “Read only. List compile errors, failing tests, detekt/ktlint violations with short proposed fixes. Don’t change code yet.”
* *Targeted fix*: “Fix only SwipeService + MatchRepository to satisfy tests. Keep WebFlux/R2DBC. No schema changes. Provide concise diff.”
* *DB change*: “Add a new Flyway migration V{N} to add X column. Update entity + repo queries accordingly. No changes to previous migrations.”

**Review checklist**

* [ ] No banned deps were added
* [ ] Tests added/updated and pass locally
* [ ] Migrations are append‑only
* [ ] No `OFFSET` in queries; cursor logic intact
* [ ] Security/privacy not weakened

---

## 8) Security & Privacy Notes

* Obfuscate precise addresses pre‑match; use `address_hash` for current home ratings.
* Basic rate limiting to be added (Redis). Until then, keep service guards conservative.
* Store images in S3‑compatible bucket (MinIO in dev). Don’t store raw binary in DB.

---

## 9) Roadmap Hints (MVP → MLP)

* **MVP**: Auth placeholder, Listings feed, Swipes, Matches (user↔user; user↔listing with owner like), Messaging basic, Ratings create, Subscription gate (fake provider ok).
* **MLP**: JWT auth + email verification, ID/phone verification flag, Map view, Filters, Owner dashboards, Rate limiting, OpenAPI generation for typed clients.

---

## 10) Glossary

* **Seek pagination**: pagination using a deterministic sort (timestamp + id) with a forward cursor. No OFFSET.
* **Gate**: permission or feature switch (e.g., subscription gate).
* **Auto‑accept**: listing flag to match immediately when a candidate likes the listing.

---

> Keep this file updated as we evolve. If code and this document diverge, **update this document in the same PR**.
