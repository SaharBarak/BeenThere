# BeenThere — Frontend Agent Playbook (Expo React Native)

> **Scope**: You operate **only in `apps/mobile/`** of a pnpm/Turbo monorepo. The backend lives at `apps/api` (Spring WebFlux + R2DBC). Follow these rules exactly.

---

## 0) Non‑negotiables

* **Do not** modify backend code or API contracts. Assume base URL: `http://localhost:8080/api/v1`.
* **TypeScript strict**. Avoid `any`; narrow types; validate with **zod** at runtime.
* **State**: **Zustand**. **Data fetching**: **TanStack Query**. **Validation**: **Zod**. **Navigation**: **React Navigation**.
* **No OFFSET** pagination. Use **cursor** strings from the API.
* Keep diffs **small and focused**; no mass reformatting; do not eject from Expo.
* Respect **subscription gating**: non‑ACTIVE users get limited preview swipes + paywall.

---

## 1) Libraries to use

Install in `apps/mobile`:

```bash
pnpm add @tanstack/react-query axios zustand zod @react-native-async-storage/async-storage
pnpm add @react-navigation/native @react-navigation/native-stack
pnpm add react-native-gesture-handler react-native-reanimated
pnpm add -D typescript @types/react @types/react-native
```

Optional (later): `expo-secure-store` for tokens.

---

## 2) Folder layout (authoritative)

```
src/
  app/
    DiscoverScreen.tsx
    ChatListScreen.tsx
    ChatThreadScreen.tsx
    RatingsScreen.tsx
    PaywallScreen.tsx
    ProfileScreen.tsx
  components/
    ListingCard.tsx
    SwipeDeck.tsx
    MatchModal.tsx
    EmptyState.tsx
  lib/
    api.ts          # axios instance, baseURL, interceptors
    query.ts        # QueryClient and provider
    env.ts          # reads EXPO_PUBLIC_API_BASE_URL or defaults
    storage.ts      # AsyncStorage helpers
  store/
    auth.ts         # user + subscription status (ACTIVE/NONE/...)
  validators/
    listing.ts
    swipe.ts
    message.ts
    rating.ts
  hooks/
    useFeed.ts
    useSwipe.ts
    useMessages.ts
  types/
    dto.ts          # mirrors server DTOs exactly
  navigation/
    RootNavigator.tsx
  theme/
    index.ts
```

---

## 3) API contracts (MVP)

* `GET /listings/feed?city=&minBudget=&maxBudget=&cursor=&limit=` → `{ items: ListingCardDTO[], nextCursor? }`
* `POST /swipes` `{ targetType:"LISTING"|"USER", targetId:uuid, action:"LIKE"|"PASS" }` → `{ matchId?: uuid }`
* `GET /billing/status` → `{ status: "ACTIVE"|"EXPIRED"|"NONE"|"CANCELED", periodEnd?: ISO }`
* (Soon) `GET /matches`, `GET/POST /matches/{id}/messages`

**Environment**: `src/lib/env.ts` must export `baseUrl` from `process.env.EXPO_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api/v1"`.

---

## 4) DTOs (src/types/dto.ts)

```ts
export type ListingType = "ROOMMATE_GROUP" | "ENTIRE_PLACE";

export interface ListingCardDTO {
  id: string;
  type: ListingType;
  title: string;
  city: string;
  price: number;
  createdAt: string; // ISO
}

export interface FeedResponse<T> { items: T[]; nextCursor?: string | null; }

export type SwipeTarget = "USER" | "LISTING";
export type SwipeAction = "LIKE" | "PASS";

export interface SwipeRequest { targetType: SwipeTarget; targetId: string; action: SwipeAction; }
export interface SwipeResponse { matchId?: string | null; }

export type SubStatus = "NONE" | "ACTIVE" | "EXPIRED" | "CANCELED";
export interface BillingStatus { status: SubStatus; periodEnd?: string; }
```

### Zod examples (src/validators/listing.ts)

```ts
import { z } from "zod";
export const listingCardSchema = z.object({
  id: z.string().uuid(),
  type: z.enum(["ROOMMATE_GROUP","ENTIRE_PLACE"]),
  title: z.string(),
  city: z.string(),
  price: z.number().int().nonnegative(),
  createdAt: z.string(),
});
export const feedResponseSchema = z.object({
  items: z.array(listingCardSchema),
  nextCursor: z.string().optional().nullable(),
});
```

---

## 5) Flows to implement (MVP)

1. **Discover + swipe**

   * `DiscoverScreen` loads feed via `useFeed({ city, min, max })` (TanStack Query + cursor).
   * `SwipeDeck` uses Reanimated + Gesture Handler. Drag right = LIKE; left = PASS; threshold triggers `POST /swipes`.
   * When `SwipeResponse.matchId` exists, show `MatchModal` with CTA (open chat later).
   * Enforce subscription gate: if not ACTIVE, allow N preview swipes → route to `PaywallScreen`.

2. **Paywall**

   * Display plan (₪20/month). For now, toggle local `SubStatus=ACTIVE` on confirm; optionally call fake webhook.

3. **Chat (stub)**

   * `ChatListScreen` + `ChatThreadScreen` placeholders; wire once endpoints land. Use cursor pagination when implemented.

4. **Ratings**

   * `RatingsScreen` with segmented control (House/Roommate/Landlord). Zod‑validate `scores` object and POST when endpoints exist.

---

## 6) Implementation details

* Centralize HTTP in `src/lib/api.ts` (axios instance + interceptors).
* Provide a `QueryClientProvider` in `src/lib/query.ts` and mount at root (RootNavigator wrapper).
* Persist minimal auth/subscription in `src/store/auth.ts` (Zustand). Keep UI‑local state out of global stores when possible.
* Cursor pagination: pass `cursor` from last response into next request; do **not** use OFFSET.

---

## 7) PR plan (strict)

**PR0 — Audit only (no code changes)**

* Output: (a) deps to add/remove, (b) tsconfig/eslint gaps, (c) missing providers/navigation, (d) list of files to create.
* Propose PR1–PR4 with file paths and acceptance criteria. Wait for approval.

**PR1 — Bootstrap & scaffolding**

* Add deps, enable TS strict, create base folder layout, `RootNavigator`, `QueryClientProvider`, and minimal screens.
* **Acceptance**: `pnpm expo start` runs; lint/typecheck pass; root screens render.

**PR2 — Data layer & contracts**

* Implement `lib/api.ts`, `lib/query.ts`, DTOs + zod validators, `useFeed`, `useSwipe`, wire `GET /billing/status` to store.
* **Acceptance**: feed call succeeds; zod narrows types; error boundary shown on invalid payload.

**PR3 — Swipe UX + paywall**

* Implement `ListingCard`, `SwipeDeck` gestures, post LIKE/PASS, match modal, subscription gating after N swipes.
* **Acceptance**: gestures hit 60fps on sim/emulator; network calls fire; gating works.

**PR4 — Ratings form**

* Implement `RatingsScreen` with zod form; POST stubs or live endpoints.
* **Acceptance**: validation errors surface; success toast on 200; error UI on 4xx.

*All PRs must keep diffs small, avoid reformat churn, and never change server DTOs.*

---

## 8) Do / Don’t

**Do** centralize network, use cursor pagination, keep state minimal, and write typed hooks.
**Don’t** eject from Expo, introduce OFFSET, modify backend/contracts, or mass‑rename files.

---

## 9) Kickoff message (paste in Cursor)

```
Use ./apps/mobile/FRONTEND_AGENT_PROMPT.md.
Read‑only first: summarize Non‑negotiables, Libraries, Folder layout, API contracts, and PR plan (PR0–PR4).
Then propose PR1–PR4 with exact file paths and acceptance criteria. Do not change code yet.
```

---

## 10) Optional: folder rules for Cursor

Create `apps/mobile/.cursorrules` with:

```md
# Cursor Rules (apps/mobile)
- Work only inside apps/mobile/.
- Follow FRONTEND_AGENT_PROMPT.md.
- Keep diffs small; no mass reformatting.
- No OFFSET pagination; use cursor strings from the API.
- Don’t modify backend or API contracts.
- TypeScript strict; use zod for runtime validation.
- State: Zustand; Data: TanStack Query; Navigation: React Navigation.

When starting a task:
1) Read FRONTEND_AGENT_PROMPT.md and summarize.
2) Propose PR1–PR4 plan and wait for approval before edits.
```
