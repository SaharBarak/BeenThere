i# BeenThere Implementation Promise & Guidelines

**Date:** 2025-01-13  
**Context:** Full implementation of BeenThere app per CLAUDE.md specifications

This document serves as both a **promise to the user** and **implementation guidelines** based on Context7 research and detailed planning.

---

## 🤝 PROMISE TO USER

I commit to implementing BeenThere exactly as specified in CLAUDE.md with:

1. **Zero deviation** from the architectural requirements
2. **Complete Context7 compliance** - all library usage backed by official docs
3. **Result Monad pattern** throughout backend services
4. **Non-blocking reactive** implementation (WebFlux + R2DBC + coroutines)
5. **Strict contract parity** between frontend and backend
6. **Hebrew RTL UI** with proper i18n
7. **Map-first approach** with single circular markers (NO heatmap)
8. **Combined rant transactions** (landlord + apartment together)
9. **Phone number privacy** (HMAC-SHA256 only, never raw storage)
10. **PR-by-PR delivery** with approval gates

**I will NOT:**
- Start coding before receiving explicit "YES" approval
- Deviate from the specified tech stack
- Use blocking operations on request paths
- Store raw phone numbers
- Implement features not in the MVP scope
- Skip the Context7 documentation requirement

---

## 📋 IMPLEMENTATION GUIDELINES (FOR CLAUDE)

### Context7 Research Results

**Backend Libraries (VERIFIED):**
- `/kotlin/kotlinx.coroutines` - Suspend functions, async/await, coroutineScope
- `/spring-projects/spring-boot` v3.3.5 - WebFlux, R2DBC, no MVC
- `/spring-projects/spring-data-relational` - ReactiveCrudRepository patterns
- `/testcontainers/testcontainers-java` v1.21.2 - PostgreSQL R2DBC testing
- `/micrometer-metrics/micrometer-docs` - Prometheus metrics integration

**Frontend Libraries (VERIFIED):**
- `/expo/expo` - React Native with development builds
- `/react-native-maps/react-native-maps` - Google provider, marker events
- `/tanstack/query` v5.71.10 - AppState integration for React Native
- `/pmndrs/zustand` - Store patterns, context integration
- `/colinhacks/zod` v3.24.2 - Schema validation, type inference
- `/i18next/i18next` - Hebrew RTL support
- `/vercel/next.js` v15.1.8 - App Router, route handlers
- `/shadcn-ui/ui` - Component installation via CLI
- `/tailwindlabs/tailwindcss.com` - Responsive utilities, RTL support

### Version Matrix (PINNED)
```
Backend:
- Kotlin: 2.0.21
- Spring Boot: 3.3.5
- kotlin-result: 2.0.0
- Testcontainers: 1.21.2

Mobile:
- Expo SDK: Latest stable
- TanStack Query: 5.71.10
- Zod: 3.24.2

Web:
- Next.js: 15.1.8
- All other libs: Latest stable
```

### Result Monad Implementation

**Error Taxonomy:**
```kotlin
sealed interface DomainError
data class ValidationError(val field: String, val message: String) : DomainError
data class AuthError(val reason: String) : DomainError
data object NotFound : DomainError
data class Conflict(val resource: String) : DomainError
data object RateLimitExceeded : DomainError
data class SubscriptionError(val message: String) : DomainError
data class DatabaseError(val cause: Throwable) : DomainError
data class UnknownError(val cause: Throwable) : DomainError
```

**HTTP Mapping:**
- ValidationError → 400
- AuthError → 401
- NotFound → 404
- Conflict → 409
- RateLimitExceeded → 429
- SubscriptionError → 403
- DatabaseError → 503
- UnknownError → 500

**Service Pattern:**
```kotlin
suspend fun operation(): Result<SuccessDTO, DomainError> = 
    validate()
        .andThen { transform() }
        .andThen { persist() }
        .map { SuccessDTO(it) }
```

### PR Implementation Plan

**PR1 — Workspace & Contracts**
- pnpm workspaces setup
- `packages/contracts` with all DTOs from CLAUDE.md §4
- `packages/ui-copy` with Hebrew strings
- Zod schemas + TypeScript types + fixtures
- **Acceptance:** `pnpm -w build` succeeds

**PR2 — Backend Skeleton**
- Spring Boot WebFlux + R2DBC + Flyway V1
- Result monad setup, error handling
- Stubs for auth/places endpoints
- BlockHound integration
- **Acceptance:** Boots, health check, Flyway runs

**PR3 — Rant Domain**
- Combined landlord+apartment rant creation
- Phone E.164 normalization + HMAC-SHA256
- Transactional writes with coroutines
- **Acceptance:** Contract fixtures succeed

**PR4 — Roommates MVP**
- Feed with cursor pagination
- LIKE/PASS swipe logic
- Match creation and chat
- **Acceptance:** Swipe→match→message works

**PR5 — Apartments Feed & Swipe**
- Listing creation (owner = creator)
- Feed with filters
- autoAccept matching logic
- **Acceptance:** Feed, swipes, matches work

**PR6 — Mobile App (Expo)**
- Map home with Google Places search
- Single circular marker → Place Profile
- Onboarding + Combined rant wizard
- RTL Hebrew UI
- **Acceptance:** iOS/Android builds work

**PR7 — Web App (Next.js)**
- Landing → Place profiles
- shadcn/ui + framer-motion
- All core features web version
- **Acceptance:** Responsive, animated

**PR8 — Observability**
- Prometheus metrics
- Structured logging
- CI/CD pipeline
- **Acceptance:** Monitoring ready

### Critical Implementation Rules

1. **NEVER start implementation before "YES" approval**
2. **Always use Context7 docs** - include API usage notes in PR descriptions
3. **Result monad at ALL service boundaries**
4. **NO blocking operations** on request paths
5. **Phone privacy:** HMAC-SHA256(E.164, secret) only
6. **Flyway append-only** - never edit past migrations
7. **Cursor pagination** - no OFFSET queries
8. **Contract parity** - backend DTOs mirror frontend exactly
9. **Hebrew RTL** - all UI text from shared i18n
10. **Map behavior:** Search → center → single circular marker → Place Profile

### Testing Requirements

- **Unit tests:** Domain services with Result patterns
- **Integration tests:** Testcontainers + PostgreSQL R2DBC
- **Contract tests:** Backend accepts frontend fixtures
- **Non-blocking verification:** BlockHound in dev/test
- **Lint enforcement:** detekt/ktlint + eslint/prettier (CI-blocking)

### Security & Privacy

- **Google ID token verification** server-side
- **Phone number storage:** HMAC-SHA256 hash only
- **CORS:** Local dev origins allowed, strict in prod
- **Rate limiting:** Rants, swipes, place snaps
- **No raw PII storage**

---

## 🎯 DELIVERABLE CHECKLIST

**Before Any Coding:**
- [x] Context7 research complete
- [x] Version matrix pinned
- [x] Result monad guide written
- [x] PR plan detailed
- [ ] User approval received ("YES")

**PR Template Requirements (Each PR):**
- [ ] Context7 library docs referenced
- [ ] API usage notes included
- [ ] Roadmap: "This PR delivers" + "Still outstanding"
- [ ] Non-blocking compliance confirmed
- [ ] Testing summary
- [ ] Risk assessment

**MVP Acceptance Criteria:**
- [ ] Contracts compile and validate
- [ ] Map: search → center → marker → Place Profile
- [ ] Rant: combined LL+apt wizard works
- [ ] Roommates: feed→swipe→match→message
- [ ] Apartments: create→feed→swipe→match
- [ ] Hebrew RTL UI throughout
- [ ] No blocking calls detected
- [ ] CI pipeline green

---

## 🚦 STOP CONDITIONS

**Implementation stops if:**
1. User provides feedback requiring architecture changes
2. Context7 docs contradict implementation approach
3. Blocking operations detected in code review
4. Contract tests fail
5. Hebrew RTL implementation issues arise

**Resume only after:**
1. Explicit user approval for changes
2. Documentation conflicts resolved
3. Non-blocking compliance restored
4. Contract parity fixed
5. i18n issues resolved

---

**This promise is binding. Implementation will follow these guidelines exactly.**

---

*Generated: 2025-01-13*  
*Based on: CLAUDE.md, README.md, Context7 research*  
*Commitment: Zero-deviation implementation*