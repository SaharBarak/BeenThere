````md
# CLAUDE.md — BeenThere (Authoritative Build Instructions)

**Read this completely _before_ writing any code.**  
All specs are **English-only**. The **app UI & copy are Hebrew (RTL)** and live in shared i18n files.

---

## 0) Mission & Scope

Build **both** frontends and a **single backend monolith**:

- **Backend (authoritative API)**: Kotlin + Spring Boot **WebFlux** + **R2DBC** (PostgreSQL), Flyway migrations, strict DTO parity with shared contracts, secrets handling, PII hashing (no raw phones), transactional coroutines, clean modular architecture.  
  **No JPA. No Spring Web MVC. No blocking JDBC on request paths (JDBC is only for Flyway).**
- **Mobile**: React Native **Expo** (iOS/Android + Expo Web for dev).
- **Web**: Next.js 14 (App Router) using **shadcn/ui** + **framer-motion** (modern, beautiful, responsive).

**Core product rules (PRD — fully binding):**
- **Home = Map-first (NO heatmap).** Search (Google Places). Selecting an address **centers the map** and drops **one circular marker**. Tapping it opens the **Apartment/Place profile**.
- **Rant flows**:
  - **Landlord + Apartment submitted together** (single “combined rant” transaction).
  - **Roommate rant** is separate.
- **Roommates swiping** (Tinder-style): LIKE/PASS → mutual LIKE → match → minimal chat (MVP).
- **Apartments feed & swiping** (separate from map): SEEK-paginated listing cards; LIKE/PASS on listings; match if `autoAccept=true` or owner already liked the seeker. (Messaging via unified matches API.)
- **Landlords can list apartments** (any authenticated user can create a listing; the creator is the owner).  
- Hebrew UI (he-IL, RTL), mobile-first microcopy.
- Optional subscription flag (₪20/month) stubs for gating swipes/messages (no live billing in MVP).

**Do not “vibe code”.** Every architecture change needs a **design note** (see §13) and explicit approval.

---

## 1) Non-Negotiable Build Rules

1) **Context7 MCP (always)**  
   Use Context7 for **every** library/framework:
   - Run `resolve-library-id` → `get-library-docs` **before** using any API.
   - Ground usage strictly in the docs. **No guessing**. Put doc refs in PR descriptions.

2) **Contracts are law**  
   - Shared `packages/contracts` (TypeScript + Zod) define request/response DTOs & validation ranges.
   - Backend DTOs mirror names & ranges exactly. Add contract tests with fixtures.

3) **Monolith backend**  
   - WebFlux + R2DBC (no MVC/JPA on request path).  
   - Result Monad (`io.github.michaelbull:kotlin-result`) at service boundaries.  
   - Ports/Adapters (hexagonal-lite), separation of concerns, domain modules.

4) **Security/PII**  
   - Never store raw landlord phone numbers. Server normalizes to E.164 and stores **HMAC-SHA256(phone, secret)** only.  
   - Verify Google ID token server-side; store `google_sub`, **not** Google tokens.

5) **Migrations**  
   - Flyway is **append-only**. Never edit past migrations.

6) **Pagination**  
   - Cursor/seek only. **No OFFSET** queries.

7) **Lint/Quality (CI-blocking)**  
   - **detekt + ktlint** for Kotlin; **eslint + prettier** for JS/TS.

8) **Approval gates**  
   - Do not introduce new tech/patterns without written OK. Present: **Problem → Current → Proposal → Engineering Benefit → Why this way**.

9) **Non-blocking guarantee**  
   - No blocking calls in request path. R2DBC only.  
   - Integrate **BlockHound (dev/test)** to detect accidental blocking.

---

## 2) Versions & Libraries (pin via Context7)

> **Pin versions via Context7** before coding; record in PR.

**Backend**
- Java 21, Kotlin **2.0.21**
- Spring Boot **3.3.5**: WebFlux, Security, Validation, Actuator
- Spring Data **R2DBC** (PostgreSQL)
- Flyway, Jackson + jackson-module-kotlin
- Result: `io.github.michaelbull:kotlin-result:2.0.0`
- Test: JUnit5, Testcontainers (postgres, r2dbc)
- Observability: Micrometer + Prometheus
- Dev guard: BlockHound (optional but recommended)

**Mobile (Expo)**
- Expo SDK (latest stable)
- TypeScript (strict)
- React Navigation (or Expo Router)
- `react-native-maps` (Google provider)
- TanStack Query, Zustand
- Zod
- i18next (he-IL), RTL
- Animations: **Reanimated** + **Moti**

**Web**
- Next.js 14 (App Router)
- TypeScript, Tailwind CSS
- **shadcn/ui** (Radix primitives)
- **framer-motion**
- TanStack Query, Zod, i18next (he-IL), RTL

---

## 3) Monorepo Layout

```txt
beenthere/
  apps/
    api/            # Kotlin monolith API
    mobile/         # Expo React Native (iOS/Android + Expo Web for dev)
    web/            # Next.js 14 + shadcn/ui + framer-motion
  packages/
    contracts/      # Zod schemas + TS types (authoritative DTOs) + fixtures
    ui-copy/        # he-IL i18n JSON (shared microcopy)
  .env.example
  CLAUDE.md         # this file
  pnpm-workspace.yaml
````

Managers: **pnpm** (JS) + **Gradle** (BE). Profiles: `dev`, `prod`, `mock`.

---

## 4) Contracts (authoritative — implement in `packages/contracts`)

> Export Zod schemas + TS types. Provide **fixtures** under `packages/contracts/examples/*`.
> **IDs**: UUID strings • **Scores**: integers **1–10** • **Dates**: `YYYY-MM-DD`.

### Common

* **PlaceRef** (must have `googlePlaceId` OR both `lat` & `lng`)

  ```ts
  { googlePlaceId?: string; formattedAddress?: string; lat?: number; lng?: number }
  ```

### Places

* `PlaceSnapReq` → `{ googlePlaceId?, formattedAddress?, lat?, lng? }`
* `PlaceSnapRes` → `{ placeId: string }`
* `PlaceProfileRes` →

  ```ts
  {
    place: { id: string; googlePlaceId?: string; formattedAddress?: string; lat?: number; lng?: number },
    ratings: {
      counts: { landlord: number; apartment: number },
      averages: { landlord?: number; apartment?: number; extras?: Record<string, number> },
      recent: Array<{
        at: string;
        landlordScores?: { fairness:number; response:number; maintenance:number; privacy:number };
        apartmentScores?: { condition:number; noise:number; utilities:number; sunlightMold:number };
        extras?: { neighborsNoise?:number; roofCommon?:number; elevatorSolar?:number;
                   neighSafety?:number; neighServices?:number; neighTransit?:number; priceFairness?:number };
        comment?: string;
      }>;
    };
  }
  ```

### Rant — **Combined Landlord + Apartment** (single submission)

* `CreateRantCombinedReq`

  ```ts
  {
    landlordPhone: string,
    periodStart?: string, periodEnd?: string,
    isCurrentResidence?: boolean,
    landlordScores: { fairness:number; response:number; maintenance:number; privacy:number },
    apartmentScores: { condition:number; noise:number; utilities:number; sunlightMold:number },
    extras?: { neighborsNoise?:number; roofCommon?:number; elevatorSolar?:number;
               neighSafety?:number; neighServices?:number; neighTransit?:number; priceFairness?:number },
    comment?: string, // ≤ 300
    place: PlaceRef
  }
  ```
* `CreateRantCombinedRes` → `{ rantGroupId: string }`

### Rant — Roommate

* `CreateRoommateRantReq`

  ```ts
  { rateeUserId?: string, rateeHint?: { name:string; org?:string },
    scores: { cleanliness:number; communication:number; reliability:number; respect:number; costSharing:number },
    comment?: string }
  ```
* `CreateRoommateRantRes` → `{ ratingId: string }`

### **Roommates Feed/Swipe/Match/Chat (MVP)**

* `GET /roommates/feed?cursor=&limit<=50&filters...`

  ```ts
  { items: Array<{ userId:string; displayName:string; photoUrl?:string; bio?:string;
                   prefs?: Record<string,unknown>; hasApartment:boolean }>,
    nextCursor?: string }
  ```
* `POST /swipes` (roommates) `{ targetUserId:string, action:'LIKE'|'PASS' }` → `{ matchId?: string }`
* `GET /matches`
* `GET /matches/{id}/messages?cursor=&limit=` → `{ items: Array<{ id:string; senderUserId:string; body:string; createdAt:string }>, nextCursor?:string }`
* `POST /matches/{id}/messages { body:string }` → `{ id:string }`

### **Apartments Feed & Swipe (REQUIRED)**

* `ListingCard`

  ```ts
  { id:string; ownerUserId:string; placeId:string; title:string; price:number;
    attrs?:Record<string,unknown>; photos:string[]; createdAt:string; autoAccept?:boolean }
  ```
* `GET /apartments/feed?cursor=&limit<=50&city?=&minPrice?=&maxPrice?=&rooms?=&furnished?=&pets?=&smoking?=`
  → `{ items: ListingCard[], nextCursor?: string }`
* `POST /listing-swipes { listingId:string, action:'LIKE'|'PASS' }` → `{ matchId?: string }`

  > A `matchId` is returned if **autoAccept=true** or the **owner has already liked** the seeker.

### **Profiles for display cards**

* **User profile** (roommate/person card details)

  * `GET /users/{id}/profile` → `{ user:{ id, displayName, photoUrl?, bio? }, ratingsSummary:{ roommateAvg?:number, count:number } }`
* **Place profile** already defined (`PlaceProfileRes`) and used by map & apartment cards.

---

## 5) Database Schema (PostgreSQL 16, R2DBC-friendly)

Use TEXT + CHECK (no native enums). Timestamps `timestamptz` UTC.
**PII**: store **only** `phone_hash` (HMAC-SHA256(E.164, secret)).

Core entities:

* `users` (email, google\_sub, display\_name, photo\_url, bio, has\_apartment, open\_to\_matching, created\_at)
* `places` (google\_place\_id unique, formatted\_address, lat/lng, attrs jsonb, created\_at)
* `landlords` (phone\_hash unique, created\_at)

**Rants**

* `rant_groups` (rater\_user\_id, landlord\_id, place\_id, period\_start/end, is\_current\_residence, comment, created\_at)
* `ratings_landlord` (rant\_group\_id FK, scores jsonb)
* `ratings_apartment` (rant\_group\_id FK, scores jsonb, extras jsonb)
* `ratings_roommate` (rater\_user\_id, ratee\_user\_id?, ratee\_hint jsonb, scores jsonb, comment, created\_at)

**Roommates**

* `roommate_swipes` (user\_id, target\_user\_id, action LIKE|PASS, unique(user\_id,target\_user\_id), created\_at)
* `roommate_matches` (a\_user\_id, b\_user\_id, unique pair, created\_at)
* `roommate_messages` (match\_id, sender\_user\_id, body, created\_at; index for seek)

**Listings (Apartments)**

* `listings` (owner\_user\_id, place\_id, title, price, attrs jsonb, auto\_accept bool default false, is\_active bool default true, created\_at)
* `listing_photos` (listing\_id, url, sort)
* `listing_swipes` (user\_id, listing\_id, action LIKE|PASS, unique(user\_id,listing\_id), created\_at)
* `listing_matches` (user\_id, listing\_id, unique pair, created\_at)

**Indexes**

* `places(google_place_id)`, foreign keys, `(created_at,id)` for feeds, swipe/match uniques.

**Flyway V1** must create all above with constraints & indexes. **Never edit V1** later.

---

## 6) Backend (Kotlin) — Modules, Patterns, Endpoints

**Architecture**: Ports/Adapters (hexagonal-lite), Result Monad at service boundaries, **coroutine transactions**, no blocking IO.

```txt
com.beenthere
  ApiApplication.kt
  config/            # ObjectMapper, Security (permitAll MVP), R2DBC tx, Profiles
  common/            # errors, result utils, cursor codec, validators
  auth/              # Google OIDC verify, JWT mint
  place/             # PlaceSnap + PlaceProfile (ports/services/repos)
  rant/              # Combined rant (LL+apt) + roommate rant
  roommates/         # feed, swipes, matches, messages
  listings/          # apartments feed, listing swipes, listing matches, CRUD (create only in MVP)
  subscription/      # feature-flag billing stubs
  api/               # controllers + mappers (strict contract mirroring)
  entities/          # R2DBC entities
  repositories/      # CoroutineCrudRepository (+ custom queries for feeds)
  mocks/             # @Profile("mock") adapters (e.g., PlaceLookupPort)
  util/              # Phone E.164 normalize + HMAC, etc.
```

**Endpoints (v1)**

Auth:

* `POST /api/v1/auth/google` → `{ idToken }` → `{ jwt, user }`

Places:

* `POST /api/v1/places/snap` → `PlaceSnapRes`
* `GET  /api/v1/places/{placeId}` → `PlaceProfileRes`

Rant:

* `POST /api/v1/rant` (combined LL+apt) → `{ rantGroupId }`
* `POST /api/v1/rant/roommate` → `{ ratingId }`

Roommates:

* `GET  /api/v1/roommates/feed?cursor=&limit=&filters=...`
* `POST /api/v1/swipes` (roommates) → `{ matchId? }`
* `GET  /api/v1/matches`
* `GET  /api/v1/matches/{id}/messages?cursor=&limit=`
* `POST /api/v1/matches/{id}/messages`

Listings (Apartments):

* `POST /api/v1/apartments` (owner creates listing; auth required)
* `GET  /api/v1/apartments/feed?cursor=&filters=...` → `ListingCard[]`
* `POST /api/v1/listing-swipes` → `{ matchId? }`

Profiles (for cards):

* `GET /api/v1/users/{id}/profile` → roommate/person profile summary

Billing (feature-flag):

* `GET /api/v1/billing/status`, `POST /api/v1/billing/webhook` (stub toggles)

**Security/PII**

* Verify Google ID token; store `google_sub`.
* Phone hashing: E.164 → HMAC with `PHONE_HASH_SECRET`.
* CORS: allow local dev origins; tighten in prod.
* Rate limits: place snap, swipes, rant creation.

**Transactions**

* Combined rant writes occur within one coroutine transaction.

**Observability**

* Micrometer + Prometheus; structured JSON logs in prod.

---

## 7) Mobile App (Expo RN) — Map-first (NO heatmap)

**Home Map**

* Search bar (Google Places).
* On selection: **center map** & **drop one circular marker**.
* Tap marker → `/place/:placeId` (fetch profile).
* If place missing: call `POST /places/snap`, then navigate.

**Onboarding**

* Google sign-in → profile (name, photo, bio ≤140) → 3 Qs → **required combined rant wizard**.

**Rant**

* Combined LL+apt (one submission), extras optional, **“I still live here”** flag.
* Roommate rant separate.
* Phone disclaimer: “number not stored, only hashed”.

**Roommates**

* Swipe deck (LIKE/PASS) → match → minimal chat.
* **Apartments feed**: listing cards with LIKE/PASS (and match if autoAccept or owner liked back).

**Tech**

* Expo + React Navigation/Router, RN Maps (Google), TanStack Query, Zustand, Zod, i18next (he), RTL.
* Animations: Reanimated + Moti.
* **Do not** use shadcn/framer-motion on RN (DOM-only).

---

## 8) Web App (Next.js 14) — shadcn/ui + framer-motion

**Pages**

* `/` Landing: address search → `/place/[id]` (can include a small map preview)
* `/place/[id]` Place profile (aggregates + recent rants)
* `/apartments` Feed + filters + swipe-like UI
* `/roommates` Swipe UI
* `/rant/new` Combined wizard
* `/signin` Google handoff (or web OIDC to backend)

**Tech**

* Next.js App Router, Tailwind, **shadcn/ui**, **framer-motion**, TanStack Query, Zod, i18next (he), RTL.
* Modern, animated, responsive.

---

## 9) i18n & Copy (Hebrew)

Store all strings in `packages/ui-copy/public/locales/he/common.json`. Examples:

```json
{
  "signin_google": "יאללה, נכנסים עם גוגל",
  "cta_rant": "יש לך מה לפרוק?",
  "open_place_profile": "לפרופיל הדירה",
  "landlord_fairness": "הוגנות בעל הבית",
  "apt_noise": "בידוד ורעש",
  "price_fairness": "מחיר מול השוק"
}
```

Frontends import from the same copy. RTL enabled globally.

---

## 10) Dev/Prod/Mock & Secrets

Backend `application.yml`:

* `dev`: local Postgres; Flyway enabled; Security permitAll (temporary).
* `prod`: env-driven; strict CORS; structured logs.
* `mock`: mock adapters (e.g., PlaceLookupPort).

Secrets via env/secret store (**never commit secrets**):

* `R2DBC_URL`, `R2DBC_USERNAME`, `R2DBC_PASSWORD`
* `JDBC_URL` (Flyway), `JDBC_USERNAME`, `JDBC_PASSWORD`
* `PHONE_HASH_SECRET`
* `GOOGLE_CLIENT_ID` (verify ID token)
* Frontend: `EXPO_PUBLIC_API_BASE_URL`, `NEXT_PUBLIC_API_BASE_URL`

---

## 11) Testing & CI

* **Contracts first**: build contracts; validate fixtures; backend contract tests must accept fixtures.
* **Unit**: domain services (Result), cursor utilities.
* **Integration**: Testcontainers (postgres + r2dbc), covering rant writes, feeds, swipes, matches, messaging.
* **Non-blocking guard**: BlockHound active in dev/test; CI fails on blocking detections.
* **Lint**: detekt + ktlint (Kotlin), eslint + prettier (JS/TS) — **CI-blocking**.
* **CI pipeline**: contracts → backend build+tests → mobile/web builds → lint/typecheck.
* **Fail CI** on any contract mismatch.

---

## 12) PR Template (Roadmap Reporting REQUIRED)

Every PR must include this header:

**Context7**

* Libraries & versions (with `resolve-library-id` results)
* Doc pointers (`get-library-docs`) and **API Usage Notes**

**Scope**

* What this PR changes (files, endpoints, contracts, migrations)

**Roadmap**

* **This PR delivers:** (bullet list)
* **Still outstanding (next PRs):** (bullet list with owners/ETA)

**Non-blocking compliance**

* Confirm no blocking calls; note any hotspots & remedies

**Testing**

* Unit + Integration summary; contract fixtures used

**Risks & Mitigations**

* e.g., migrations, rollbacks, feature flags

---

## 13) Design Review Format (Approval Gate)

Before introducing anything beyond this baseline:

1. **Problem** — user/job or tech gap
2. **Current state** — what exists now
3. **Proposed solution** — modules/patterns/endpoints/migrations
4. **Engineering benefit** — perf, reliability, DX, testability
5. **Why this way** — alternatives, trade-offs, cost
6. **Contracts impact** — DTO changes + migration plan

No implementation starts until I approve **in writing**.

---

## 14) Plan of Work (PRs) — Propose, then implement after approval

> **Do not implement before a “YES”.** Each PR must include Context7 outputs.

**PR1 — Workspace & Contracts**

* pnpm workspaces; `packages/contracts` with **all** DTOs in §4 and fixtures.
* `packages/ui-copy` with he-IL strings.
* **Acceptance**: `pnpm -w build` OK; fixtures validate; types export.

**PR2 — Backend Skeleton**

* Spring Boot WebFlux + R2DBC + Flyway V1 (schema §5).
* Profiles (`dev`/`prod`/`mock`), secrets via env; health endpoints.
* Stubs: `POST /auth/google`, `POST /places/snap`, `GET /places/{id}`.
* **Acceptance**: boots; Flyway runs; `/actuator/health` green; BlockHound enabled in dev/test.

**PR3 — Rant Domain**

* Services + controllers: **`POST /rant`** (combined) & **`POST /rant/roommate`**.
* Phone E.164 + HMAC; transactional writes; Result→HTTP mapping.
* Contract tests use fixtures.
* **Acceptance**: example payloads succeed; rows present.

**PR4 — Roommates MVP**

* `GET /roommates/feed` (seek), `POST /swipes` (LIKE/PASS), matches, messages.
* Rate limits on swipes/messages.
* **Acceptance**: swipe→match→message happy paths pass.

**PR5 — Apartments Feed & Swipe + Listings Create**

* `POST /apartments` (owner creates), `GET /apartments/feed` (seek), `POST /listing-swipes`.
* Match on `autoAccept=true` or prior owner like; unify matches/messages endpoints.
* **Acceptance**: feed cards work; swipes persist; match returns when conditions met.

**PR6 — Mobile App (Expo)**

* Onboarding (sign-in/profile/3 Qs) + **combined rant wizard**.
* **Map home**: search → center & **one circular marker** → Place Profile.
* Roommates swipe deck + chat; Apartments feed swipe.
* **Acceptance**: iOS/Android flows OK; RTL & he-IL verified.

**PR7 — Web App (Next + shadcn/ui + framer-motion)**

* Landing w/ address search → `/place/[id]`.
* Place profile, apartments feed (swipe-like), roommate swipe.
* Polished animations, responsive.
* **Acceptance**: pages render; a11y basics; animations present.

**PR8 — Observability & Hardening**

* Prometheus metrics, structured logs, CI badges, error tracking hooks, rate-limit tuning.
* **Acceptance**: dashboards/metrics OK; CI green; non-blocking checks pass.

---

## 15) Acceptance Checklist (MVP must meet all)

* Contracts compile; fixtures validate and are **accepted by backend**.
* Map home (mobile): search → center & **one circular marker** → Place Profile.
* **Rant**: combined LL+apt wizard end-to-end; roommate rant works.
* **Roommates**: feed→swipe→match→message works.
* **Apartments**: listings create; feed→swipe; match on autoAccept or owner like.
* Profiles show **general ratings/bio/data** for users and places.
* Secrets via env; phone HMAC; Flyway append-only; **no blocking** on request path.
* Lint (detekt/ktlint & eslint/prettier) **CI-blocking**; CI pipelines pass; metrics exposed.

---

## 16) Kickoff Prompt (paste in editor)

> Read CLAUDE.md fully.
>
> 1. Run Context7 for all libs in §2 and pin versions.
> 2. Propose a PR1–PR8 plan with file trees, deps, schemas, migrations, and **Roadmap (done/left)**. **Wait for my YES.**
> 3. After approval, implement **PR1 only**, then pause for review.
> 4. Repeat per PR with Context7-backed **API Usage Notes** and the PR Template in §12.

```
::contentReference[oaicite:0]{index=0}
```
