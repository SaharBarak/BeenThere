# Epic: Testing & CI Infrastructure

## Problem Statement

The backend has growing service/repository code but lacks:
- Automated tests (unit + integration)
- CI pipeline for PRs
- Database testing with real PostgreSQL
- Code coverage tracking

Without these, bugs slip through and refactoring becomes risky.

## Proposed Solution

### 1. Testing Framework
- **Unit tests**: JUnit 5 + MockK for service layer
- **Integration tests**: Testcontainers + PostgreSQL
- **Contract tests**: Validate API responses match DTOs

### 2. CI Pipeline (GitHub Actions)
```yaml
on: [push, pull_request]
jobs:
  test:
    - Checkout
    - Setup JDK 21
    - Start Testcontainers
    - Run ./gradlew test
    - Upload coverage to Codecov
  lint:
    - ktlint check
```

### 3. Code Quality
- ktlint for Kotlin style
- Detekt for static analysis
- Coverage threshold: 60% (services), 80% (utils)

## Affected Components

- `apps/api/src/test/` (new test directory)
- `.github/workflows/ci.yml` (new)
- `apps/api/build.gradle.kts` (test dependencies)

## Technical Spec

### Test Dependencies
```kotlin
testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
testImplementation("io.mockk:mockk:1.13.9")
testImplementation("org.testcontainers:postgresql:1.19.3")
testImplementation("org.testcontainers:junit-jupiter:1.19.3")
```

### Test Structure
```
apps/api/src/test/kotlin/com/beenthere/
├── unit/
│   ├── services/
│   │   ├── AuthServiceTest.kt
│   │   ├── RantServiceTest.kt
│   │   └── RoommatesServiceTest.kt
│   └── util/
│       └── PhoneUtilsTest.kt
├── integration/
│   ├── BaseIntegrationTest.kt
│   ├── AuthFlowTest.kt
│   └── RantCreationTest.kt
└── contract/
    └── ApiContractTest.kt
```

## Success Criteria

- [ ] `./gradlew test` passes with >60% coverage
- [ ] GitHub Actions runs on every PR
- [ ] Testcontainers spins up PostgreSQL for integration tests
- [ ] Failed tests block PR merge
- [ ] Coverage badge in README

## Assignees

- **Keeper** (QA/Testing lead)
- **Core** (Backend support)

## Estimated Effort

1-2 sprints

---

*Tests are the safety net that lets us ship fast and refactor fearlessly.*
