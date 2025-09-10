# PRD — BeenThere (React Native, Hebrew UI)

## 1) Vision & Scope

A Hebrew, RTL, mobile-first app helping renters:

* **Search apartments** (by address/place and browse lists)
* **Search roommates** (with/without apartment)
* **Rant** (rate landlords, apartments, roommates)
* **Match** (Tinder-style roommates)
* **Map-first** home with search → center & **one circular marker** → open **apartment profile**

**Phase-1 (MVP)**: RN (Expo), Google sign-in, profile, initial rant wizard, **map + address search + circular marker + apartment profile**, create/view ratings, roommate swiping MVP (basic match chat).
**Non-goals Phase-1**: payments (except optional gating stub), push notifications, advanced chat, full landlord verification.

> Pricing note (from original vision): optional **₪20/month** subscription to enable swipes/chat beyond a free cap. Keep as a **feature flag**; real billing can be Phase-2.

---

## 2) Personas

1. **Apartment seeker** (מחפש/ת דירה)
2. **Roommate with apartment** (שותף/פה עם דירה)
3. **Frustrated renter** (רוצה לפרוק/לדרג)

---

## 3) Success Metrics (Phase-1)

* Activation: % completing **Google sign-in → profile → first rant**
* Content: # ratings/day (landlord/apartment/roommate)
* Search utility: search → apartment profile tap-through rate
* Matching: right-swipes/day, matches/day
* Map engagement: % sessions with **address search + marker tap**

---

## 4) End-to-End Flows

### A) Onboarding (Hebrew, RTL)

1. **Google sign-in (only)**
2. **Renter profile**: display name, quick photo (camera/upload), short bio (≤140)
3. **Status (≤3 Qs)**

   * יש לך דירה? (כן/לא)
   * מה מחפשים? (דירה / שותפים / רק לפרוק)
   * פתוח/ה למאצ’ינג? (כן/לא)
4. **Initial Rant Wizard (required)**: last/current renting experience (single submission = **landlord+apartment together**)

   * **Landlord** scores 1–10: fairness, responsiveness, maintenance, privacy
   * **Apartment** scores 1–10: condition, noise/insulation, utilities (water/power/pressure), sunlight/mold
   * **Extras** (optional fields requested): neighbors noise, roof/common areas, solar/elevator flags; neighborhood (safety, services, transit); price fairness at the time
   * Identify **landlord by phone** (E.164 input; server stores **HMAC** only)
   * Identify **apartment by place** (Google Places autocomplete → our DB “snap”)
   * Checkbox **“I still live here”**

### B) Home = Map

* **Top search bar** (Google Places Autocomplete).
* On selecting an address: **center the map** there and **drop a single circular marker**.
* Tapping the circle → **Apartment (Place) Profile** screen (aggregated ratings & info).
* Bottom floating actions (FABs): **Apartments**, **Roommates**, **Rant**.

### C) Apartments (List → Details)

* Filters: price range, city/area, pets, smoking, rooms, furnished.
* Card: photos, price, location, area notes (from community), short desc, contact (if exists).
* Show related rants tied to that place.

### D) Roommates (Tinder-like)

* Swipe deck: photo, bio, prefs (pets/smoking/schedule), has-apt flag.
* **Right = LIKE**, **Left = PASS**; mutual → **match**.
* **Chat**: single thread per match (text only, MVP).

### E) Rant (Vent)

* Choose **“Landlord + Apartment (together)”** or **“Roommate”**.
* Scores 1–10, optional comment (≤300).
* Landlord tagged by **phone** (HMAC only), Apartment by **place** + period.
* Roommate by **profile** or **hint** (name + org).

---

## 5) UI & Copy (Hebrew)

* RTL; fonts: **Rubik/Heebo**; currency **₪**; locale **he-IL**.
* Tone: friendly, community-first.

Examples (keys → Hebrew values):

* `signin_google`: “יאללה, נכנסים עם גוגל”
* `cta_rant`: “יש לך מה לפרוק?”
* `landlord_fairness`: “הוגנות בעל הבית”
* `apt_noise`: “בידוד ורעש”
* `neighbors_noise`: “רעש שכנים”
* `roof_common`: “גג/שטחים משותפים”
* `elevator_solar`: “מעלית/סולארי”
* `neigh_safety`: “בטיחות בשכונה”
* `neigh_services`: “שירותים קרובים”
* `neigh_transit`: “תחבורה ציבורית”
* `price_fairness`: “מחיר מול השוק”
* `open_place_profile`: “לפרופיל הדירה”

---

## 6) Information Architecture (App Screens)

* `/signin`
* `/onboarding/profile`
* `/onboarding/rant` (wizard)
* `/home/map` (default after onboarding) — map + search
* `/apartments` (list + filters)
* `/roommates` (swipe deck)
* `/rant/new`
* `/place/:placeId` — **Apartment/Place Profile (from the map circle)**
* `/landlord/:tag` — landlord profile (by phone\_hash)
* `/user/:id` — roommate profile

---

## 7) Functional Requirements (Phase-1)

* Google OAuth (sign-in only)
* Profile create/edit (name, photo, bio)
* Initial rant wizard (required)
* **Map home with search → center & circle marker → place profile**
* Add rants (landlord+apartment together; roommate)
* Roommates: swipe + match (MVP chat)
* Show apartment/landlord aggregates on profiles

**Non-Functional**

* Mobile performance: initial screen < 2.5s on mid-tier device
* Accessibility: RTL, touch ≥ 44px, contrast AA
* Privacy: never store raw phone numbers; E.164 → HMAC(SHA-256 + server secret); deletion/export hooks Phase-2

---

# Architecture

## A) Mobile App (React Native, Expo)

**Stack**

* Expo SDK, TypeScript (strict)
* **React Navigation** (stack + tabs)
* **react-native-maps** (Google provider)
* Google Places Autocomplete (via Places API; client-side for UX)
* **TanStack Query** (network/cache), **Zustand** (local state)
* **Zod** (validation; import shared contracts when possible)
* i18n: `i18next` (he-IL), RTL enabled

**Key modules**

```
apps/mobile/src/
  app/
    HomeMap/MapScreen.tsx          // map + search bar + single circle marker
    PlaceProfile/PlaceProfile.tsx   // opened by tapping the circle
    Onboarding/{Profile.tsx, RantWizard/*}
    Roommates/{SwipeDeck.tsx, Chat.tsx}
    Apartments/{List.tsx, Filters.tsx, Details.tsx}
    Rant/{NewRant.tsx, NewRoommateRant.tsx}
  components/{SearchBar.tsx, CircularMarker.tsx, RatingSliders.tsx, PhoneInput.tsx}
  lib/{api.ts, auth.ts, places.ts, env.ts}
  state/{user.ts, rantDraft.ts}
  i18n/{he.json}
  validators/{index.ts}
```

**Home Map behavior (NO heatmap)**

* Search (Google Places) → receive `place_id + coords`
* Center map to `coords` and **drop one circular marker** component
* Tap marker → navigate to `/place/:placeId`
* If the place doesn’t exist in our DB yet, call **`POST /places/snap`** to create it (see API)

**Auth**

* Expo Google sign-in → obtain Google ID token → send to backend **`POST /auth/google`** → get app JWT.

---

## B) Backend (Kotlin, Spring Boot WebFlux + R2DBC)

**Principles**

* Reactive only (WebFlux + coroutines), no blocking on request path
* Result Monad at service boundaries (`io.github.michaelbull:kotlin-result:2.0.0`)
* Flyway migrations (append-only)
* Profiles: `dev`, `prod`, `mock`

**Core Endpoints (v1)**

* **Auth**

  * `POST /auth/google` → `{ idToken }` → `{ jwt, user }`
* **Places / Map**

  * `POST /places/snap` → `{ googlePlaceId, formattedAddress?, lat?, lng? }` → `{ placeId }`
    *Upserts* a place in our DB so the map’s circle tap has a profile to open.
  * `GET /places/by-google/{googlePlaceId}` → `{ placeId }` (helper)
  * `GET /places/{placeId}` → **Apartment/Place Profile**: `{ place, ratings: { landlordAvg, apartmentAvg, count, recent[] } }`
* **Rant (Ratings)**

  * `POST /rant` (**combined landlord+apartment**)
    Body includes: landlordPhone, period, landlordScores, apartmentScores (+optional neighbors/roof/solar/neighborhood/priceFairness), comment, place ref
  * `POST /rant/roommate`
* **Roommates (MVP)**

  * `GET /roommates/feed?cursor=&filters=`
  * `POST /swipes` `{ targetUserId, action }` → `{ matchId? }`
  * `GET /matches` / `GET /matches/{id}/messages` / `POST /matches/{id}/messages`
* **Aggregates**

  * `GET /landlords/{tag}` (where `tag = phone_hash`)
  * `GET /users/{id}` (roommate profile + received ratings)

**Optional (Feature-flag)**

* **Subscriptions** (₪20/month):

  * `GET /billing/status`
  * `POST /billing/webhook` (stub provider toggles ACTIVE/EXPIRED)
    Gate swipes/messages if enabled; free tier has daily cap.

---

## C) Data Architecture (PostgreSQL, R2DBC-friendly)

> IDs UUID; timestamps `timestamptz` UTC.
> Use TEXT + CHECKs instead of native ENUMs.
> **Phone privacy**: store **HMAC\_SHA256(E.164, SECRET)** only.

**Reference**

```sql
users( id uuid pk, email text unique, google_sub text unique,
       display_name text, photo_url text, bio text,
       has_apartment bool default false, open_to_matching bool default true,
       created_at timestamptz default now() );

places( id uuid pk, google_place_id text unique,
        formatted_address text, city text,
        lat double precision, lng double precision,
        attrs jsonb default '{}'::jsonb,
        created_at timestamptz default now() );

landlords( id uuid pk, phone_hash text unique not null,
           created_at timestamptz default now() );
```

**Rant (Ratings)**
(landlord + apartment together; roommate separate)

```sql
-- Combined “event” data attached to both landlord & apartment:
rant_groups(
  id uuid pk,
  rater_user_id uuid not null references users(id),
  landlord_id uuid not null references landlords(id),
  place_id uuid not null references places(id),
  period_start date, period_end date,
  is_current_residence bool default false,
  comment text,
  created_at timestamptz default now()
);

ratings_landlord(
  id uuid pk,
  rant_group_id uuid not null references rant_groups(id) on delete cascade,
  scores jsonb not null     -- {fairness:int, response:int, maintenance:int, privacy:int}
);

ratings_apartment(
  id uuid pk,
  rant_group_id uuid not null references rant_groups(id) on delete cascade,
  scores jsonb not null     -- {condition:int, noise:int, utilities:int, sunlightMold:int}
  , extras jsonb            -- optional: {neighborsNoise:int?, roofCommon:int?, elevatorSolar:int?, neighSafety:int?, neighServices:int?, neighTransit:int?, priceFairness:int?}
);

ratings_roommate(
  id uuid pk,
  rater_user_id uuid not null references users(id),
  ratee_user_id uuid references users(id),
  ratee_hint jsonb,         -- {name, org?}
  scores jsonb not null,    -- {cleanliness, communication, reliability, respect, costSharing}
  comment text,
  created_at timestamptz default now()
);

-- Roommates matching (MVP)
roommate_swipes( id uuid pk, user_id uuid not null references users(id),
                 target_user_id uuid not null references users(id),
                 action text not null check (action in ('LIKE','PASS')),
                 created_at timestamptz default now(),
                 unique(user_id, target_user_id) );

roommate_matches( id uuid pk, a_user_id uuid not null references users(id),
                  b_user_id uuid not null references users(id),
                  created_at timestamptz default now(),
                  unique(a_user_id, b_user_id) );

roommate_messages( id uuid pk, match_id uuid not null references roommate_matches(id) on delete cascade,
                   sender_user_id uuid not null references users(id),
                   body text not null, created_at timestamptz default now(),
                   index(match_id, created_at, id) );
```

**Indexes**

* `places(google_place_id)`, `ratings_*` on subject IDs
* `roommate_swipes(user_id, target_user_id)`, `roommate_matches(a_user_id,b_user_id)`

---

## D) Contracts (authoritative shapes)

**Create Rant: Landlord + Apartment (together)**

```json
POST /api/v1/rant
{
  "landlordPhone": "+972501234567",
  "periodStart": "2024-11-01",
  "periodEnd": "2025-08-31",
  "isCurrentResidence": true,
  "landlordScores": { "fairness": 7, "response": 5, "maintenance": 6, "privacy": 8 },
  "apartmentScores": { "condition": 6, "noise": 4, "utilities": 7, "sunlightMold": 5 },
  "extras": {
    "neighborsNoise": 6,
    "roofCommon": 7,
    "elevatorSolar": 6,
    "neighSafety": 7,
    "neighServices": 8,
    "neighTransit": 7,
    "priceFairness": 6
  },
  "comment": "בסדר גמור, לפעמים איטי בתיקונים.",
  "place": {
    "googlePlaceId": "ChIJN1t_tDeuEmsRUsoyG83frY4",
    "formattedAddress": "..."
  }
}
→ 201 { "rantGroupId": "uuid" }
```

**Create Rant: Roommate**

```json
POST /api/v1/rant/roommate
{
  "rateeUserId": "uuid-optional",
  "rateeHint": { "name": "דני", "org": "טכניון" },
  "scores": { "cleanliness": 6, "communication": 8, "reliability": 7, "respect": 8, "costSharing": 7 },
  "comment": "אחלה, רק לשטוף כלים יותר מהר"
}
→ 201 { "ratingId": "uuid" }
```

**Map + Place Profile (NO heatmap)**

```json
POST /api/v1/places/snap
{ "googlePlaceId": "ChIJ...", "formattedAddress": "...", "lat": 32.795, "lng": 35.203 }
→ 200 { "placeId": "uuid" }

GET /api/v1/places/{placeId}
→ 200 {
  "place": { "id": "uuid", "googlePlaceId": "ChIJ...", "formattedAddress": "...", "lat": 32.795, "lng": 35.203 },
  "ratings": {
    "counts": { "landlord": 12, "apartment": 18 },
    "averages": { "landlord": 6.8, "apartment": 6.2, "extras": { "neighborsNoise": 5.7, "neighSafety": 7.1 } },
    "recent": [
      { "at": "2025-08-01T10:20:00Z", "landlordScores": {...}, "apartmentScores": {...}, "extras": {...}, "comment": "..." }
    ]
  }
}
```

**Roommates (MVP)**

```json
GET /api/v1/roommates/feed?cursor=opaque&hasApartment=true
→ 200 { "items": [ { "userId":"uuid","displayName":"...", "bio":"...", "prefs":{...}, "hasApartment":true } ], "nextCursor":"..." }

POST /api/v1/swipes
{ "targetUserId": "uuid", "action": "LIKE" }
→ 200 { "matchId": "uuid-or-null" }
```

**Auth**

```json
POST /api/v1/auth/google
{ "idToken": "google-id-token" }
→ 200 { "jwt": "app-jwt", "user": { "id":"uuid", "displayName":"..." } }
```

**(Optional) Billing**

```json
GET /api/v1/billing/status → { "status":"ACTIVE|NONE|EXPIRED", "periodEnd":"2025-12-01T00:00:00Z" }
POST /api/v1/billing/webhook → flips status (stub)
```

---

## E) Security & Privacy

* Verify Google ID token server-side; store `google_sub` (not tokens).
* Phone numbers: input E.164 on client; server computes **HMAC\_SHA256** with secret, stores **hash only**.
* Rate-limit: creating rants, swipes, and place snapping.
* Phase-2: profanity filter, DSR (export/delete).

---

## F) Observability & Analytics

* Backend: Micrometer + Prometheus.
* Mobile: basic analytics — sign-in, initial rant complete, address search, marker tap, profile viewed, swipe, match.

---

## G) Implementation Plan

**Sprint 1 — Foundations**

* Expo RN scaffold (TS strict, RTL, i18n he-IL), Google sign-in flow, profile screen.
* Backend skeleton (WebFlux + R2DBC + Flyway + Result Monad), `/auth/google`, `/places/snap`, `/places/{id}`.
* Shared contracts package (Zod) for FE/BE parity (optional but recommended).

**Sprint 2 — Initial Rant Wizard**

* RN wizard (landlord+apartment together, extras + “I still live here”).
* Backend: `POST /rant` with transactional creation (`rant_groups` + `ratings_landlord` + `ratings_apartment`).
* Place selection inside wizard: autocomplete → snap place if needed.

**Sprint 3 — Home Map**

* RN map screen with search bar; select address → center & **one circular marker**; tap → **Place Profile**.
* Backend: `/places/{id}` aggregates. No heatmap.

**Sprint 4 — Roommates MVP**

* Swipe deck + `POST /swipes` + match list + minimal chat.
* Backend: swipes, matches, messages.

**Sprint 5 — Apartments List & Polish**

* Apartments list/filters + details (if available data), error states, accessibility pass.
* Optional: subscriptions gating stub, rate-limits tuning, analytics dashboards.

---

### Minimal Signup Questions (from your request)

1. יש לך דירה? (כן/לא)
2. מה אתם/ן מחפשים? (דירה / שותפים / רק לפרוק)
3. פתוח/ה למאצ’ינג? (כן/לא)

---

If you want, I can also output **ready-to-paste contract schemas** (Zod) for the `extras` fields and the `isCurrentResidence` flag, plus the exact **Flyway migration** snippet for `extras jsonb` and `is_current_residence`.
