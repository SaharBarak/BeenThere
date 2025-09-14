Got it — we’re turning **roommates matching** into a two-sided swipe between a **Roommate Apartment Profile (group)** and a **seeker**. Here’s exactly what to build across **UX, data, APIs, and matching rules** (fits our existing architecture).

# 1) UX / Screens (additions & tweaks)

**For a roommate who has an apartment (group owner/member):**

* **Create Roommate Apartment** (wizard under: Profile → My Listings → New)

    * Place (Google), photos, title, description
    * **Type = ROOMMATE\_GROUP**
    * **Capacity** (total beds/rooms), **Spots available**, **Move-in date**
    * **Group prefs** (quiet hours, pets, smoking, cleanliness, gender pref)
    * **Auto-accept** (optional)
* **Manage Members**: add all current roommates (bubbles on card)
* **Candidates** (inbox): see seekers who liked the group; **swipe** (LIKE/PASS) on candidates
* **Matches & Chat**: per match thread (group ↔ seeker)

**For a seeker (looking to join a roommates apartment):**

* **Swipe Deck — Apartments**: cards show **apartment photos** + **roommate bubbles** (profile pics), short **bio/description**, **stats** (ratings from place & members)
* **Apartment Profile**: full details; can LIKE (apply)
* **Matches & Chat**: when the group also likes them

# 2) Data model (minimal deltas)

**`listings`** (we already have it) — add/group fields:

* `type` = `'ROOMMATE_GROUP' | 'ENTIRE_PLACE'`
* `capacity_total` INT
* `spots_available` INT
* `move_in_date` DATE NULL
* `rent_per_room` INT NULL (optional)
* `attrs` JSONB extended with group prefs:

  ```json
  {
    "prefs": {
      "quietHours": "22:00-07:00",
      "petsAllowed": true,
      "smokingAllowed": false,
      "cleanlinessLevel": 7,
      "genderPref": "ANY"   // ANY | FEMALE | MALE | MIXED
    }
  }
  ```
* `auto_accept` BOOLEAN (already present)

**`listing_members`** (we already created):

* `role` `'OWNER'|'TENANT'` (exists)
* `is_current` BOOLEAN (exists)
* (optional) `display_order` INT

**No new tables required**. We’ll **reuse**:

* `ratings_apartment` (by `place_id`)
* `ratings_roommate` (by each member `user_id`)
* `swipes`/**`listing_swipes`** + `matches` (kind `USER_LISTING`)
* `messages` (chat tied to `match_id`)

**Indexes (if missing):**

* `listings(type, is_active, created_at desc)`
* `listing_members(listing_id, is_current)`
* Keep ratings & matches indexes as already defined.

**Flyway migration (delta example):**

```sql
-- V3__roommate_group_fields.sql
ALTER TABLE listings
  ADD COLUMN IF NOT EXISTS capacity_total int,
  ADD COLUMN IF NOT EXISTS spots_available int,
  ADD COLUMN IF NOT EXISTS move_in_date date,
  ADD COLUMN IF NOT EXISTS rent_per_room int;
-- attrs JSONB already exists; group prefs live under attrs.prefs
CREATE INDEX IF NOT EXISTS idx_listings_type_active ON listings(type, is_active, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_listing_members_current ON listing_members(listing_id) WHERE is_current = true;
```

# 3) API surface (precise)

### Create / manage the Roommate Apartment Profile (group)

* **POST `/api/v1/apartments`**

  ```json
  {
    "type": "ROOMMATE_GROUP",
    "placeId": "uuid",
    "title": "דירת שותפים מוארת",
    "description": "שקטה, ליד תחב״צ",
    "price": 4200,
    "rentPerRoom": 2100,
    "capacityTotal": 3,
    "spotsAvailable": 1,
    "moveInDate": "2025-10-01",
    "attrs": { "prefs": { "quietHours": "22:00-07:00", "petsAllowed": true, "smokingAllowed": false, "cleanlinessLevel": 7, "genderPref": "ANY" } },
    "photos": ["https://.../1.jpg","https://.../2.jpg"],
    "autoAccept": false
  }
  ```
* **POST `/api/v1/listings/{id}/members`** (add roommate)

  ```json
  { "userId": "uuid", "role": "TENANT", "isCurrent": true }
  ```
* **DELETE `/api/v1/listings/{id}/members/{userId}`**

### Feeds

* **Seeker → swipe groups:**
  **GET** `/api/v1/apartments/feed?type=ROOMMATE_GROUP&cursor=&limit=`
  Returns **cards** with: `photos[]`, `roommates[]` (current members bubbles), `stats { apartmentAvg, landlordAvg, roommatesAvg, counts }`.
* **Group → review seekers (candidates):**
  **GET** `/api/v1/listings/{id}/candidates?cursor=&limit=`
  Returns seeker profiles who liked/apply-liked the listing (and not yet decided by group).

### Swipes (two-sided)

* **Seeker likes a group:**
  **POST** `/api/v1/swipes`

  ```json
  { "targetType": "LISTING", "targetId": "listing-uuid", "action": "LIKE" }
  ```
* **Group likes a seeker (by any member with permission):**
  **POST** `/api/v1/swipes`

  ```json
  { "contextListingId": "listing-uuid", "targetType": "USER", "targetId": "user-uuid", "action": "LIKE" }
  ```
* Response (both directions):

  ```json
  { "matchId": "uuid" }           // when mutual OR listing.autoAccept=true
  ```

  If PASS: `{ "matchId": null }`.

### Profile & Chat

* **GET** `/api/v1/apartments/{listingId}` → full roommate apartment profile (photos, description, members with bios & roommateRating summary, stats)
* **GET** `/api/v1/matches` (includes USER\_LISTING matches)
* **GET** `/api/v1/matches/{id}/messages` | **POST** messages

# 4) Matching rules (deterministic)

1. **Seeker → Listing LIKE**

    * Upsert swipe (`unique(user_id, targetType, target_id)`).
    * If `listing.autoAccept = true` → create match immediately.
    * Else check if **any current member** (OWNER/TENANT) has already swiped LIKE on this seeker **for this listing** (via `contextListingId`) → if yes, create match.
2. **Listing (member) → Seeker LIKE**

    * Upsert swipe with `contextListingId = listingId` and `targetType = USER`.
    * If seeker had already liked the listing → create match.
3. **Match creation**

    * Insert into `matches` with `kind = 'USER_LISTING'`, `a_user_id = seekerId`, `b_subject_type = 'LISTING'`, `b_subject_id = listingId` (idempotent via unique constraint).
    * **Chat**: 1 thread per match. **Participants**: the seeker + any listing member may send messages (we already store `sender_user_id` per message; no extra table needed).

**Permissions**

* Only **listing members** (`OWNER` or `TENANT`) can swipe on behalf of the listing (we pass `contextListingId`, validate membership).
* **Rate limits** (MVP+): guard spam on both sides.

# 5) Cards & Profile content (what the UI needs)

**Roommate Apartment Card (for seeker swipe deck / list):**

* **hero photo** (first photo), **photo count**
* **roommate bubbles** (up to 5): `userId`, `displayName`, `photoUrl`
* title, short description, city/area, **price/rentPerRoom**
* **stats**:

    * `apartmentAvg` & count (from `ratings_apartment` by `place_id`)
    * `landlordAvg` & count (if listing has `landlord_id`; else fallback by `place_id`)
    * `roommatesAvg` & count (aggregate `ratings_roommate` for current members)
* badges: `spotsAvailable`, `moveInDate`, prefs (pets/smoking/quietHours)

**Full Apartment Profile:**

* gallery, description, attrs/prefs
* members: avatar, displayName, short bio, **roommateRating summary** (avg,count)
* same `stats` block as card, plus link to place page if needed

# 6) Acceptance checks

* Creating a **ROOMMATE\_GROUP listing** shows up in **seeker feed** with: photos, roommate bubbles, stats populated.
* A seeker **LIKEs** a listing → if group **LIKEs** back (or auto-accept) → **match** created, chat opens.
* Group **candidates** endpoint lists seekers who liked the listing and need a decision.
* All DB ops are **R2DBC** on request path; Flyway runs via JDBC on boot; non-blocking verified.

If you want, I can also spit out a tiny SQL seed + sample JSON payloads for both sides to smoke-test the flow.
