-- BeenThere Database Schema V1
-- Uses TEXT + CHECK constraints (R2DBC friendly, no native enums)
-- All timestamps are timestamptz UTC

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    google_sub VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    photo_url TEXT,
    bio TEXT CHECK (LENGTH(bio) <= 300),
    has_apartment BOOLEAN NOT NULL DEFAULT FALSE,
    open_to_matching BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Places table (addresses with Google Place data)
CREATE TABLE places (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    google_place_id VARCHAR(255) UNIQUE,
    formatted_address TEXT,
    lat NUMERIC(10, 8),
    lng NUMERIC(11, 8),
    attrs JSONB DEFAULT '{}',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT place_ref_check CHECK (
        google_place_id IS NOT NULL OR (lat IS NOT NULL AND lng IS NOT NULL)
    )
);

-- Landlords table (phone numbers are hashed for privacy)
CREATE TABLE landlords (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_hash VARCHAR(64) NOT NULL UNIQUE, -- HMAC-SHA256(E.164 format, secret)
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Rant groups (combined landlord + apartment ratings)
CREATE TABLE rant_groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rater_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    landlord_id UUID NOT NULL REFERENCES landlords(id),
    place_id UUID NOT NULL REFERENCES places(id),
    period_start DATE,
    period_end DATE,
    is_current_residence BOOLEAN NOT NULL DEFAULT FALSE,
    comment TEXT CHECK (LENGTH(comment) <= 300),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Landlord ratings (part of rant group)
CREATE TABLE ratings_landlord (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rant_group_id UUID NOT NULL REFERENCES rant_groups(id) ON DELETE CASCADE,
    scores JSONB NOT NULL, -- {fairness: 1-10, response: 1-10, maintenance: 1-10, privacy: 1-10}
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Apartment ratings (part of rant group)
CREATE TABLE ratings_apartment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rant_group_id UUID NOT NULL REFERENCES rant_groups(id) ON DELETE CASCADE,
    scores JSONB NOT NULL, -- {condition: 1-10, noise: 1-10, utilities: 1-10, sunlightMold: 1-10}
    extras JSONB DEFAULT '{}', -- Optional: {neighborsNoise: 1-10, roofCommon: 1-10, etc.}
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Roommate ratings (separate from rant groups)
CREATE TABLE ratings_roommate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rater_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    ratee_user_id UUID REFERENCES users(id) ON DELETE SET NULL, -- Can be null if user deleted
    ratee_hint JSONB, -- {name: "string", org?: "string"} for non-users
    scores JSONB NOT NULL, -- {cleanliness: 1-10, communication: 1-10, reliability: 1-10, respect: 1-10, costSharing: 1-10}
    comment TEXT CHECK (LENGTH(comment) <= 300),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Roommate swipes
CREATE TABLE roommate_swipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    action TEXT NOT NULL CHECK (action IN ('LIKE', 'PASS')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_roommate_swipe UNIQUE (user_id, target_user_id)
);

-- Roommate matches (bidirectional)
CREATE TABLE roommate_matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    a_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    b_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_roommate_match UNIQUE (a_user_id, b_user_id),
    CONSTRAINT no_self_match CHECK (a_user_id != b_user_id),
    CONSTRAINT ordered_match CHECK (a_user_id < b_user_id) -- Ensure consistent ordering
);

-- Roommate messages
CREATE TABLE roommate_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id UUID NOT NULL REFERENCES roommate_matches(id) ON DELETE CASCADE,
    sender_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    body TEXT NOT NULL CHECK (LENGTH(body) BETWEEN 1 AND 1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Apartment listings
CREATE TABLE listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    place_id UUID NOT NULL REFERENCES places(id),
    price INTEGER NOT NULL CHECK (price > 0), -- Price in integer cents/agorot
    attrs JSONB DEFAULT '{}', -- {rooms: int, bathrooms: int, furnished: bool, pets: bool, etc.}
    auto_accept BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Listing photos
CREATE TABLE listing_photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Listing swipes (users swiping on apartment listings)
CREATE TABLE listing_swipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    action TEXT NOT NULL CHECK (action IN ('LIKE', 'PASS')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_listing_swipe UNIQUE (user_id, listing_id)
);

-- Listing matches (user <-> listing owner)
CREATE TABLE listing_matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE, -- The seeker
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT unique_listing_match UNIQUE (user_id, listing_id)
);

-- Subscription status (feature flag for billing)
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    status TEXT NOT NULL DEFAULT 'NONE' CHECK (status IN ('NONE', 'ACTIVE', 'EXPIRED')),
    period_start TIMESTAMPTZ,
    period_end TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =====================================================
-- INDEXES for performance (especially seek pagination)
-- =====================================================

-- Users
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_google_sub ON users(google_sub);

-- Places
CREATE INDEX idx_places_google_place_id ON places(google_place_id);
CREATE INDEX idx_places_location ON places(lat, lng);

-- Rant groups
CREATE INDEX idx_rant_groups_rater ON rant_groups(rater_user_id);
CREATE INDEX idx_rant_groups_place ON rant_groups(place_id);
CREATE INDEX idx_rant_groups_landlord ON rant_groups(landlord_id);

-- Roommate operations (seek pagination)
CREATE INDEX idx_roommate_swipes_user_created ON roommate_swipes(user_id, created_at DESC, id DESC);
CREATE INDEX idx_roommate_matches_user_created ON roommate_matches(a_user_id, created_at DESC, id DESC);
CREATE INDEX idx_roommate_matches_user_b_created ON roommate_matches(b_user_id, created_at DESC, id DESC);
CREATE INDEX idx_roommate_messages_match_created ON roommate_messages(match_id, created_at DESC, id DESC);

-- Listings (seek pagination)
CREATE INDEX idx_listings_active_created ON listings(is_active, created_at DESC, id DESC);
CREATE INDEX idx_listings_owner ON listings(owner_user_id);
CREATE INDEX idx_listing_swipes_user_created ON listing_swipes(user_id, created_at DESC, id DESC);
CREATE INDEX idx_listing_matches_user_created ON listing_matches(user_id, created_at DESC, id DESC);

-- Photo ordering
CREATE INDEX idx_listing_photos_listing_sort ON listing_photos(listing_id, sort_order);

-- Ratings queries
CREATE INDEX idx_ratings_roommate_ratee ON ratings_roommate(ratee_user_id);
CREATE INDEX idx_ratings_roommate_rater ON ratings_roommate(rater_user_id);