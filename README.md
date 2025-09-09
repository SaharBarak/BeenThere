# BeenThere

Swipe-first marketplace to find **roommates** or **entire apartments**. Users, landlords, and roommates can rate each other; swiping unlocks a match and chat. Participation costs **₪20/month**.

---

## Monorepo layout

```
.
├─ apps/
│  ├─ mobile/        # Expo React Native (TypeScript)
│  └─ api/           # Kotlin + Spring Boot (WebFlux, R2DBC)
├─ infra/
│  └─ docker/docker-compose.yml   # Postgres (+ optional pgAdmin)
├─ .github/workflows/ci.yml       # CI for API + Mobile
├─ ARCHITECTURE.md                # Monorepo-level guidance
└─ apps/api/ARCHITECTURE.md       # API-only architecture (source of truth)
```

---

## Tech stack

**Backend**: Kotlin 2 • Spring Boot 3.3 • WebFlux • R2DBC (PostgreSQL) • Flyway • Jackson Kotlin • Micrometer/Prometheus

**Mobile**: Expo • React Native • TypeScript (strict) • React Navigation • TanStack Query • Zustand • Zod

**Infra**: Docker Compose (PostgreSQL 16)

---

## Quickstart (TL;DR)

### 1) Start Postgres

```bash
docker compose -f infra/docker/docker-compose.yml up -d db
```

### 2) Run the API (dev)

```bash
cd apps/api
./gradlew bootRun
```

* API runs at **[http://localhost:8080](http://localhost:8080)**
* Base path: **/api/v1** (e.g., `/api/v1/listings/feed`)

### 3) Run the Mobile app (dev)

```bash
cd ../../apps/mobile
pnpm install
# Optionally set the API base URL for mobile
# echo 'EXPO_PUBLIC_API_BASE_URL="http://localhost:8080/api/v1"' > .env
pnpm expo start
```

Open on iOS simulator, Android emulator, or Expo Go.

> If you change the API port or host, update `EXPO_PUBLIC_API_BASE_URL` accordingly.

---

## Configuration

### API (`apps/api/src/main/resources/application.yml`)

* R2DBC (for WebFlux) and JDBC (for Flyway) are both configured:

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/beenthere
    username: postgres
    password: postgres
  datasource:
    url: jdbc:postgresql://localhost:5432/beenthere
    username: postgres
    password: postgres
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Mobile (`apps/mobile`)

* Reads API base from `EXPO_PUBLIC_API_BASE_URL` (fallback `http://localhost:8080/api/v1`).
* Global providers: QueryClientProvider (TanStack Query) and React Navigation.

---

## Database & migrations

* Schema managed by **Flyway** under `apps/api/src/main/resources/db/migration/`.
* **Append-only**: never edit old migrations; add `V{N}__description.sql`.
* Postgres 16 is provided via Docker Compose.

---

## Architecture (high level)

* **API**: Reactive stack (WebFlux + R2DBC). No JPA. Cursor (seek) pagination only (no OFFSET).
* **Core entities**: users, profiles, listings (+photos), swipes, matches, messages, subscriptions, ratings.
* **Key flows**: feed → swipe (LIKE/PASS) → match (mutual/auto-accept) → chat; ratings captured for houses/roommates/landlords.

For all details, see:

* `apps/api/ARCHITECTURE.md` — API-only source of truth.
* `ARCHITECTURE.md` — Monorepo guidance and collaboration rules.

---

## CI / Quality

* GitHub Actions runs for API + Mobile: lint, typecheck, tests, build.
* API: `detekt`, `ktlint`, `test`, and `build` must pass.
* Mobile: `eslint` + TypeScript strict.

---

## Developer workflow

1. Create a branch: `feat/...` or `fix/...`.
2. Keep PRs small (≤ \~200 LOC), single-purpose, and test-backed.
3. **Do not** modify old Flyway migrations.
4. Use cursor pagination (no OFFSET) and keep the stack reactive.

### Useful commands

```bash
# API
cd apps/api
./gradlew detekt ktlintCheck test build --no-daemon
./gradlew bootRun

# Mobile
cd ../mobile
pnpm -w lint
pnpm -w typecheck
pnpm expo start
```

---

## Working with AI assistants (Cursor/Claude/GPT)

* Follow the rules in `apps/mobile/FRONTEND_AGENT_PROMPT.md` for the frontend.
* Follow `apps/api/ARCHITECTURE.md` (invariants, DB rules) for the backend.
* Folder-scoped `.cursorrules` files restrict changes and load playbooks.
* Require a **plan** before edits; add/adjust tests; then implement minimal diffs.

**Claude kickoff (frontend)**

```
Use ./apps/mobile/FRONTEND_AGENT_PROMPT.md. Read-only first: summarize the rules and propose PR1–PR4 with exact file paths. Do not change code yet.
```

**Claude kickoff (backend)**

```
Read apps/api/ARCHITECTURE.md. Read-only first: list build/lint errors, failing tests, and drift from the doc. Propose ≤5 small PRs with files to touch. Do not change code yet.
```

---

## Troubleshooting

### Docker credential helper error

```
error getting credentials - err: exec: "docker-credential-desktop": executable file not found
```

Fix by installing macOS keychain helper or disabling the creds store:

* Install helper: `brew install docker-credential-helpers` and set `"credsStore":"osxkeychain"` in `~/.docker/config.json`.
* Or remove the creds store entirely (not needed for public images).

### Compose race crash (concurrent map writes)

If compose crashes when pulling in parallel:

```bash
docker pull postgres:16
export COMPOSE_PARALLEL_LIMIT=1
docker compose -f infra/docker/docker-compose.yml up -d
```

### Postgres not reachable

* Ensure container is healthy: `docker ps`, `docker logs beenthere-db`.
* Verify port `5432` not taken; adjust mapping if needed.

### Expo cache issues

* Reset: press `r` in Expo CLI, or run `expo start -c`.

---

## License

TBD — add a license before public distribution.

---

## Maintainers

* Core: API (Kotlin/WebFlux), Mobile (Expo RN). Codeowners and PR template recommended to keep standards consistent.
