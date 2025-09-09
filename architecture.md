Below are **copy‑paste ready** files to add to the repo. Paths are relative to the repository root.

---

## `ARCHITECTURE.md`

# BeenThere — Architecture & Working Agreements

**Monorepo** powering BeenThere: a swipe‑based marketplace for apartments and roommate constellations.

---

## 1) Overview

* **apps/mobile**: Expo React Native (TypeScript, strict) — swipe UI, chat, ratings, paywall.
* **apps/api**: Spring Boot (Kotlin 2, WebFlux, R2DBC) — listings, swipes, matches, messages, ratings, billing webhook.
* **infra**: Dev dependencies (Postgres via Docker Compose).
* **.github/workflows**: CI for API + Mobile.

**Principles**

* Reactive end‑to‑end (WebFlux + R2DBC). No JPA, no Web MVC.
* Schema is source of truth via **Flyway** (append‑only migrations).
* **Seek pagination** everywhere (`created_at, id` → base64 cursor). No `OFFSET`.
* Small, test‑backed PRs. Lint + tests are blocking.

---

## 2) Tech & Versions

* Java **21**, Kotlin **2.0.x**, Spring Boot **3.3.x**
* Postgres **16**
* Mobile: Expo SDK (latest), RN + TS (strict)

---

## 3) Backend (API)

### 3.1 Dependencies

* `spring-boot-starter-webflux`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `spring-boot-starter-actuator`
* `spring-boot-starter-data-r2dbc`, `org.postgresql:r2dbc-postgresql`
* `spring-boot-starter-jdbc` + `flyway-core` (JDBC only for migrations) + `org.postgresql:postgresql`
* `jackson-module-kotlin`
* `micrometer-registry-prometheus`
* (optional) `springdoc-openapi-starter-webflux-ui`

### 3.2 Packages

```
com.beenthere
  ├─ auth/            # (soon) JWT filters, guards
  ├─ common/          # enums, error types, cursor codecs
  ├─ user/            # User, Profile
  ├─ listing/         # Listing, ListingPhoto, feed queries
  ├─ swipe/           # Swipe upsert + queries
  ├─ match/           # Match & Message
  ├─ rating/          # Ratings (house, roommate, landlord)
  ├─ subscription/    # Subscription state
  └─ api/             # Controllers + DTOs
```

### 3.3 Data Model (MVP)

* `users(id, email, password_hash, role, created_at)`
* `profiles(user_id, display_name, age, bio, budget_min, budget_max, city, prefs jsonb)`
* `listings(id, owner_user_id, type['ROOMMATE_GROUP'|'ENTIRE_PLACE'], title, city, price, attrs jsonb, auto_accept, is_active, created_at)`
* `listing_photos(id, listing_id, url, sort)`
* `subscriptions(id, user_id unique, status['NONE'|'ACTIVE'|'EXPIRED'|'CANCELED'], period_start, period_end, provider, provider_ref, created_at)`
* `swipes(id, user_id, target_type['USER'|'LISTING'], target_id, action['LIKE'|'PASS'], created_at, unique(user_id,target_type,target_id))`
* `matches(id, kind['USER_USER'|'USER_LISTING'], a_user_id, b_subject_type['USER'|'LISTING'], b_subject_id, created_at, unique(a_user_id,b_subject_type,b_subject_id))`
* `messages(id, match_id, sender_user_id, body, created_at)`
* `ratings_*` tables for house, roommate, landlord

**Indexes**: feed/ranking, auditing, uniqueness (see Flyway files).

### 3.4 Core Flows

**Swipe → Match**

* Upsert swipe (`ON CONFLICT ... DO UPDATE`).
* If `LIKE`:

    * USER↔USER: match only if the other user already liked you.
    * USER↔LISTING: match if `auto_accept=true`, else require listing owner liked the user.
* Uniqueness on matches ensures idempotency.

**Feed**

* Filter: active listings, unswiped by viewer, budget overlap, same city.
* Order: `created_at DESC, id DESC`. Return `limit+1` to compute next cursor.

**Subscriptions**

* Only ACTIVE subscribers can swipe/message (checked in services).

### 3.5 Invariants

* No JPA/Web MVC. No blocking DB ops on request path.
* Cursor pagination only; no OFFSET.
* JSONB fields represented as `String` in entities (parsed at edges).
* Append‑only Flyway. Never edit old migration files.

---

## 4) Mobile (Expo RN)

* RN + TS (strict), Zustand, TanStack Query, Zod, React Navigation, Axios.
* Swipe deck (Reanimated + Gesture Handler), limited free preview, paywall for full access.
* Types and validators match API DTOs 1:1.

---

## 5) Local Dev

**Infra**

```
docker compose -f infra/docker/docker-compose.yml up -d db
```

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

---

## 6) CI & PR Policy

* CI blocks on: `detekt`, `ktlint`, `test` (API) and `eslint`, `typecheck` (mobile).
* Keep PRs small and single‑purpose. No changes to existing Flyway migrations.
* Security: expose only `health,info,metrics,prometheus` from Actuator.

---

## 7) AI Collaboration Rules (Cursor/Claude/GPT)

* MAY edit: controllers, services, repos, DTOs, new migrations, tests.
* MUST NOT: add JPA/Web MVC; edit/delete past migrations; break API shapes without plan; introduce OFFSET.
* Workflow: propose plan → add/adjust tests → implement with small diffs.

---

## 8) Roadmap (MVP → MLP)

* MVP: Auth placeholder, feed, swipes, matches, messages basic, ratings create, subscription gate (fake provider ok).
* MLP: JWT auth, verification, map view, filters, owner dashboards, rate limits, OpenAPI client generation.

---

## 9) References

* Model Context (collab protocol & invariants): keep in sync with this file.

---

## `/.github/workflows/ci.yml`

name: CI

on:
push:
branches: \[ "main", "develop" ]
pull\_request:
branches: \[ "main", "develop" ]

concurrency:
group: ci-\${{ github.ref }}
cancel-in-progress: true

jobs:
api:
name: API (Kotlin/WebFlux)
runs-on: ubuntu-latest
steps:
\- name: Checkout
uses: actions/checkout\@v4

```
  - name: Setup Java 21
    uses: actions/setup-java@v4
    with:
      distribution: temurin
      java-version: '21'

  - name: Gradle cache & build
    uses: gradle/gradle-build-action@v3
    with:
      build-root-directory: apps/api
      arguments: detekt ktlintCheck test build --no-daemon
```

mobile:
name: Mobile (Expo RN)
runs-on: ubuntu-latest
steps:
\- name: Checkout
uses: actions/checkout\@v4

```
  - name: Setup Node
    uses: actions/setup-node@v4
    with:
      node-version: '20'

  - name: Setup pnpm
    uses: pnpm/action-setup@v3
    with:
      version: 9

  - name: Install deps
    run: pnpm install --frozen-lockfile

  - name: Lint & typecheck (workspace)
    run: |
      pnpm -w lint
      pnpm -w typecheck
```
