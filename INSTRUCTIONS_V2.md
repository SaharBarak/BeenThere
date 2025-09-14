# BeenThere — Remaining Work PRD (Backend & Integration)

> **Purpose:** A precise, do-this-next plan for Claude Code to take the backend from “routes compile & run” to **fully working, non-blocking MVP** with DB, JWT, enriched apartment/roommate feeds, and contract-verified responses.
>
> **Read with**: `CLAUDE.md` + `definition.md`. **Use Context7** for every library, especially **kotlin-result**.

---

## 0) Current State (recap)

- ✅ Monorepo in place (`apps/api`, `packages/contracts`, …)
- ✅ 15+ endpoints scaffolded (controllers/services/repos wired)
- ✅ WebFlux + R2DBC stack compiles, boots
- ✅ Entities & repositories mapped; Flyway scripts present
- ⚠️ DB not connected in `dev`
- ⚠️ JWT/OIDC not finalized (temporary header used)
- ⚠️ Feed/profile **enrichment** (photos, roommate bubbles, rating stats) needs verified SQL + tests
- ⚠️ Testcontainers & BlockHound not enforcing non-blocking in CI

---

## 1) Critical Path to MVP (must complete)

### A) **Database Bring-Up (dev)**
**Deliver:**
- Working `docker compose` for Postgres 16 under `infra/docker/docker-compose.yml`
- `application-dev.yml` using **R2DBC** for runtime + **JDBC** for Flyway (JDBC **not** on request path)
- Flyway auto-migrates on boot (**V1** base + **V2** listing_members/landlord_id/description)
- Optional dev seeder (profile `dev`) to create: user(owner), place, landlord, listing(+photos), listing_members, a few ratings

**Acceptance:**
- `bootRun --spring.profiles.active=dev` → `/actuator/health` reports **UP** (DB + Flyway ok)
- Basic `SELECT 1` via R2DBC succeeds

---

### B) **Security: Replace header with JWT (Google OIDC → App JWT)**
**Deliver:**
- `/api/v1/auth/google`: verify Google ID token server-side (OIDC), upsert `users(google_sub, email, display_name, photo_url)`
- Mint **our** JWT (RSA) with `sub=userId`, `iss`, `aud`, `exp`
- Resource server config to **require JWT** for all non-public routes
- Public routes: `/api/v1/auth/google`, `/actuator/health` (others authenticated)

**Acceptance:**
- Calling any protected endpoint without JWT → 401
- With valid JWT → 200; `Principal` resolves to correct `userId`

---

### C) **Apartments Feed Enrichment (cards)** — **photos + roommate bubbles + stats**
**Deliver:**
- `GET /api/v1/apartments/feed?cursor=&limit=&filters...` returns **`ListingCard[]`** per contracts:
    - `photos: string[]` (sorted by `sort`, limit 5)
    - `roommates: [{userId,displayName,photoUrl}]` (from **listing_members** where `is_current=true`, up to 5)
    - `stats`: aggregates:
        - **apartmentAvg**, **apartmentCount** (from `ratings_apartment` by `place_id`)
        - **landlordAvg**, **landlordCount** (by `landlord_id` if present, else fallback by `place_id`)
        - **roommatesAvg**, **roommatesCount** (roommate ratings over current member `user_id`s)
- Seek pagination on `(created_at,id)` with opaque cursor

**Acceptance:**
- First item has `photos[]`, `roommates[]`, and `stats.*` populated when data exists
- Cursor returns stable, de-duplicated pages
- Query path is **R2DBC**, no blocking; BlockHound clean

---

### D) **Full Apartment Profile**
**Deliver:**
- `GET /api/v1/apartments/{listingId}` returns **`ApartmentProfileRes`**:
    - `listing{ id, ownerUserId, placeId, title, description, price, attrs, photos[], createdAt, autoAccept }`
    - `place{ id, googlePlaceId?, formattedAddress?, lat?, lng? }`
    - `roommates[{ userId, displayName, photoUrl?, bio?, roommateRating{avg?,count} }]`
    - `stats{ apartmentAvg?, apartmentCount, landlordAvg?, landlordCount, roommatesAvg?, roommatesCount }`

**Acceptance:**
- Response includes **bios** for roommates and their **roommateRating summary**
- Averages/counts match DB fixtures (exact numbers)
- 404 on unknown `listingId`

---

### E) **Listing Swipes & Matches (owner ↔ seeker)**
**Deliver:**
- `POST /api/v1/listing-swipes { listingId, action:'LIKE'|'PASS' }`:
    - On LIKE: create match if `autoAccept=true` **or** owner liked user
    - Idempotent upsert of swipe rows; unique constraints enforced
- Matches visible in unified `/api/v1/matches`

**Acceptance:**
- Right-swipe yields `{matchId}` when conditions met; PASS yields `{matchId:null}`
- Duplicate swipe doesn’t create duplicates; constraints tested

---

### F) **Testcontainers + BlockHound**
**Deliver:**
- Integration tests spin **Postgres** via Testcontainers
- Seed data programmatically or SQL; verify feed/profile/matches paths return enriched DTOs
- BlockHound enabled in dev/test; fails on any accidental blocking

**Acceptance:**
- CI runs ITs successfully
- Intentional blocking call (simulated) triggers BlockHound failure (guard proven)

---

## 2) Contract Parity (enforced)

**Must match `packages/contracts` exactly.** Re-verify or update contracts for:

- `ListingCard`
- `ApartmentProfileRes`
- `PlaceSnap*`, `PlaceProfileRes`
- `CreateRantCombined*`, `CreateRoommateRant*`
- Roommates feed/swipe/match/message DTOs

**Acceptance:**
- Contract tests: fixtures under `packages/contracts/examples` deserialize to BE DTOs and pass validation
- Any contract change includes migration/compat note

---

## 3) Quality, Observability, Ops

### Logging & Metrics
- JSON logs in `prod`, console in `dev`
- `/actuator/prometheus` with app counters (swipes, matches, rants created)

### Lint & CI
- detekt + ktlint blocking in CI
- ESlint + Prettier for TS packages
- CI order: **contracts → backend build & tests (incl. Testcontainers & BlockHound) → mobile/web build**

### Secrets & Privacy
- `.env.example` updated (DB creds, JWT keys, PHONE_HASH_SECRET, GOOGLE_CLIENT_ID)
- Phone handling: **E.164 normalize → HMAC_SHA256**; raw numbers never persisted/logged

---

## 4) Nice-to-Have (after MVP lock)

- Dev seeder via `@Profile("dev")` service (non-blocking)
- Rate limiting for messages/swipes (sliding window)
- S3/MinIO signed upload for listing photos (keep stub in MVP)
- Trust score weighting in feed ranking
- OpenAPI doc generation (for web client scaffolding)

---

## 5) Deliverables per Work Item (granular)

### W1 — DB Bring-Up
- Files: `infra/docker/docker-compose.yml`, `application-dev.yml`
- Verify: Flyway V1+V2 apply, `/actuator/health` UP

### W2 — JWT/OIDC
- Files: `auth/*`, security config, RSA keys for dev in config
- Verify: 401/200 behavior, JWT payload contains `sub=userId`

### W3 — Apartments Feed Enrichment
- Files: `listings/*` service + repo custom SQL, DTO mappers
- Verify: First page items include photos/bubbles/stats; cursor stable

### W4 — Apartment Profile Endpoint
- Files: `listings/*` profile query, roommate rating summaries
- Verify: Full `ApartmentProfileRes` shape with bios & stats

### W5 — Listing Swipes + Matches
- Files: `listings/swipes`, `matches/*`, constraints/migrations (if needed)
- Verify: Idempotent swipes; match creation rules honored

### W6 — IT & Non-Blocking Guards
- Files: `src/test/...` (Testcontainers), BlockHound enablement
- Verify: CI green; blocking causes test fail

---

## 6) Acceptance Checklist (MVP lock)

- **DB:** dev DB up, migrations applied on boot
- **Auth:** Google OIDC → app JWT; protected endpoints enforce JWT
- **Feed:** `/apartments/feed` returns **photos**, **roommate bubbles**, **stats** (apt/landlord/roommates)
- **Profile:** `/apartments/{id}` returns full `ApartmentProfileRes` incl. bios & roommate rating summary
- **Swipes/Matches:** listing LIKE creates `{matchId}` per rules; duplicates prevented
- **Rants:** combined & roommate endpoints functional with transaction + HMAC phone
- **Non-Blocking:** BlockHound clean; R2DBC only on request path
- **Contracts:** fixtures pass; BE DTOs mirror Zod schemas exactly
- **CI:** detekt/ktlint + Testcontainers + contracts all pass

---

## 7) Kickoff Prompt for Claude Code (paste in editor)
ou are Claude Code inside the BeenThere repo.

Goal: Finish the MVP backend per “Remaining Work PRD”.

MANDATORY — Context7 MCP:

For ALL libs used in this phase (Spring Security JWT, Nimbus JOSE, Testcontainers, R2DBC, kotlin-result) run resolve-library-id then get-library-docs.

Especially for io.github.michaelbull:kotlin-result:2.0.0 — re-read and update RESULT_GUIDE if needed.

Deliver in this order, pausing after each for review:
A) W1 DB Bring-Up (compose, application-dev.yml, Flyway V1+V2 on boot). Show commands & logs.
B) W2 JWT/OIDC (Google verify → app JWT; security config). Provide sample curl showing 401→200.
C) W3 Apartments Feed Enrichment (cards with photos/bubbles/stats). Include SQL and example JSON.
D) W4 Apartment Profile Endpoint (full shape with bios & roommate rating summaries).
E) W5 Listing Swipes & Matches (idempotent; autoAccept path). Include constraints.
F) W6 Testcontainers + BlockHound (prove non-blocking; CI step).

For each W*, include:

Files touched (paths)

Contract references (types)

SQL (if any)

Tests (unit + IT)

Acceptance proof (sample responses)

STOP after each W* until I reply “YES”.
