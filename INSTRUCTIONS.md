You are Claude Code working inside the beenthere monorepo.

READ THESE FILES FIRST (no coding yet):
- ./CLAUDE.md  (authoritative build instructions)
- ./README.md  (if present)

ABSOLUTE RULES
- Do NOT start implementing the Kotlin backend until you:
  1) Use Context7 MCP to fetch the official docs for **io.github.michaelbull:kotlin-result** (v2.0.0)
  2) Produce a written "Result Monad Usage Guide" tailored to this project
  3) Get explicit approval on that guide

- Non-blocking only: Spring WebFlux + R2DBC + coroutines.
  No JPA. No Spring Web MVC. JDBC is only for Flyway migrations.
- Contracts (packages/contracts, Zod) are the single source of truth.
- Use Result Monad at service boundaries; map errors to HTTP in one place.
- Flyway is append-only.
- Lint is CI-blocking (detekt/ktlint + eslint/prettier).
- Use Context7 MCP for every library (resolve + docs). No guessing.

MANDATORY CONTEXT7 STEPS (execute now, before planning code)
1) resolve-library-id for:
   - io.github.michaelbull:kotlin-result:2.0.0
   - spring-boot 3.3.5 (webflux, security, validation, actuator)
   - spring-data-r2dbc (postgres), r2dbc-postgresql
   - kotlinx-coroutines, kotlinx-coroutines-reactor
   - jackson-module-kotlin
   - testcontainers (postgres, r2dbc)
   - micrometer + prometheus
   - expo sdk (mobile), react-native-maps, tanstack-query, zustand, zod, i18next
   - next.js 14, shadcn/ui, framer-motion, tailwind

2) get-library-docs for each resolved ID.

3) In your response, paste:
   - The resolved versions you will pin
   - Doc pointers/links (from Context7)
   - Short “API Usage Notes” per lib (exact functions/components you’ll use and constraints)

DELIVERABLES (NO BACKEND CODE YET)
A) **PR Plan (PR1..PR8)** matching CLAUDE.md §14:
   - File trees per PR
   - Dependencies added per PR
   - Endpoints/DTOs/migrations touched per PR
   - Acceptance checks per PR
   - Each PR must include a Roadmap section with:
     * “This PR delivers”
     * “Still outstanding (next PRs)”
   - Explicit note that all server code is non-blocking (and BlockHound in dev/test)

B) **Result Monad Usage Guide** (project-specific) to be added as:
   - `apps/api/docs/RESULT_GUIDE.md`
   Include:
   1. **Core Types & Operators** you will use with examples grounded in docs:
      - `Result<V, E>`, `Ok`, `Err`
      - `map`, `mapError`, `andThen`, `fold`, `recover`, `getOrElse`
      - Interop with suspending functions (no blocking)
   2. **Error Taxonomy** (sealed interfaces) for our domains:
      - `ValidationError`, `AuthError`, `NotFound`, `Conflict`, `SubscriptionError`, `RateLimitExceeded`, `DatabaseError`, `UnknownError`
      - Rules: never throw for expected paths; model as `Err`
   3. **HTTP Mapping Table** (single place) for controller layer:
      - e.g. `ValidationError → 400`, `AuthError → 401`, `NotFound → 404`, `Conflict → 409`, `RateLimitExceeded → 429`, `SubscriptionError → 403`, `DatabaseError → 503`
      - JSON error shape:
        `{ "error": { "code": "VALIDATION_FAILED", "message": "...", "details": {...}, "traceId": "..." } }`
   4. **Service Boundary Pattern**:
      - All domain services are `suspend` and return `Result<SuccessDTO, DomainError>`
      - Repositories return `Result<Entity, RepoError>`; map repo errors to domain errors in services
      - Composition via `andThen` for multi-step flows (e.g., PlaceSnap → CombinedRant write)
   5. **Controller Pattern**:
      - Controllers call service once, `fold` to success JSON or error mapping
      - No `try/catch` for expected flows; exceptions only for truly unexpected failures
   6. **Testing Strategy**:
      - Unit tests per service use `Ok/Err` fixtures to assert branching
      - Contract tests ensure DTO parity with `packages/contracts` fixtures
   7. **Do/Don’t** checklists aligning with CLAUDE.md (no blocking, no OFFSET, etc.)

C) **Result Quick Quiz (self-check)** — include answers inline:
   - Q1: When to use `andThen` vs `map`?
   - Q2: How to convert a `Result<DTO, Error>` to HTTP response without exceptions?
   - Q3: What is your policy for unexpected exceptions in WebFlux + coroutines?
   - Q4: Show a tiny pseudo-flow: validate → upsert → aggregate using `andThen`.
   - Q5: How do you avoid blocking when hashing phone numbers and verifying Google ID tokens?

D) **Pin Versions**:
   - Produce a version matrix (from Context7) you will use:
     * Kotlin, Spring Boot, kotlin-result, r2dbc-postgresql, coroutines, jackson-kotlin, testcontainers, micrometer, etc.
     * Expo SDK, RN Maps, Next 14, shadcn/ui, framer-motion, TanStack, Zod, i18next
   - Do NOT create files yet; just list versions here for my review.

STOP CONDITION
- Stop after producing A–D above.
- Do NOT write any backend Kotlin code until I reply “YES” to the Result Guide.
- After approval:
  - Implement **PR1 only** (workspace + contracts + ui-copy) exactly per CLAUDE.md §14.
  - Then pause for review again.

REMINDERS (from CLAUDE.md you must follow later)
- Map-first home: search centers map, drop one circular marker, tap → Place profile (no heatmap).
- Combined Rant (LL+Apartment single transaction) + Roommate Rant.
- Roommates: feed + swipe + match + minimal chat.
- Apartments: create listing; apartments feed + swipe; match if autoAccept or owner like.
- Profiles show general ratings/bio/data when listing/person is shown.
- Landlords can list apartments (any authenticated user; creator is owner).
- All server code is non-blocking; BlockHound in dev/test.
- Lint is CI-blocking.

Now produce:
1) Context7 resolution results + doc pointers + API Usage Notes.
2) PR1..PR8 plan as specified.
3) RESULT_GUIDE outline with examples and the HTTP mapping table.
4) Answers to the Result Quick Quiz.

Then wait for my approval.
