-- BeenThere Database Schema V1
-- Following CLAUDE.md specifications for PostgreSQL 16 with R2DBC

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL UNIQUE,
    google_sub TEXT NOT NULL UNIQUE,
    display_name TEXT NOT NULL,
    photo_url TEXT,
    bio TEXT CHECK (length(bio) <= 140),
    has_apartment BOOLEAN NOT NULL DEFAULT false,
    open_to_matching BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Places table
CREATE TABLE places (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    google_place_id TEXT UNIQUE,
    formatted_address TEXT,
    lat DOUBLE PRECISION,
    lng DOUBLE PRECISION,
    attrs JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT places_location_check CHECK (
        (google_place_id IS NOT NULL) OR (lat IS NOT NULL AND lng IS NOT NULL)
    )
);

-- Landlords table (PII protection: only store hashed phone)
CREATE TABLE landlords (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone_hash TEXT NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Rant groups (combined landlord + apartment rants)
CREATE TABLE rant_groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rater_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    landlord_id UUID NOT NULL REFERENCES landlords(id) ON DELETE CASCADE,
    place_id UUID NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    period_start DATE,
    period_end DATE,
    is_current_residence BOOLEAN NOT NULL DEFAULT false,
    comment TEXT CHECK (length(comment) <= 300),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Landlord ratings (part of rant group)
CREATE TABLE ratings_landlord (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rant_group_id UUID NOT NULL REFERENCES rant_groups(id) ON DELETE CASCADE,
    scores JSONB NOT NULL
);

-- Apartment ratings (part of rant group)
CREATE TABLE ratings_apartment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rant_group_id UUID NOT NULL REFERENCES rant_groups(id) ON DELETE CASCADE,
    scores JSONB NOT NULL,
    extras JSONB
);

-- Roommate ratings (separate from rant groups)
CREATE TABLE ratings_roommate (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rater_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    ratee_user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    ratee_hint JSONB,
    scores JSONB NOT NULL,
    comment TEXT CHECK (length(comment) <= 300),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT roommate_rating_target_check CHECK (
        ratee_user_id IS NOT NULL OR ratee_hint IS NOT NULL
    )
);

-- Roommate swipes
CREATE TABLE roommate_swipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    action TEXT NOT NULL CHECK (action IN ('LIKE', 'PASS')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(user_id, target_user_id)
);

-- Roommate matches (mutual likes)
CREATE TABLE roommate_matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    a_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    b_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT roommate_match_order CHECK (a_user_id < b_user_id),
    UNIQUE(a_user_id, b_user_id)
);

-- Roommate messages
CREATE TABLE roommate_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    match_id UUID NOT NULL REFERENCES roommate_matches(id) ON DELETE CASCADE,
    sender_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    body TEXT NOT NULL CHECK (length(body) <= 1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Listings (apartments)
CREATE TABLE listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    place_id UUID NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    title TEXT NOT NULL CHECK (length(title) <= 100),
    price INTEGER NOT NULL CHECK (price > 0),
    attrs JSONB,
    auto_accept BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Listing photos
CREATE TABLE listing_photos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    url TEXT NOT NULL,
    sort INTEGER NOT NULL DEFAULT 0
);

-- Listing swipes
CREATE TABLE listing_swipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    action TEXT NOT NULL CHECK (action IN ('LIKE', 'PASS')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(user_id, listing_id)
);

-- Listing matches
CREATE TABLE listing_matches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(user_id, listing_id)
);

-- Indexes for performance (seek pagination and common queries)

-- Users
CREATE INDEX idx_users_google_sub ON users(google_sub);
CREATE INDEX idx_users_created_at_id ON users(created_at, id);

-- Places
CREATE INDEX idx_places_google_place_id ON places(google_place_id);
CREATE INDEX idx_places_location ON places(lat, lng);

-- Rant groups and ratings
CREATE INDEX idx_rant_groups_rater_user_id ON rant_groups(rater_user_id);
CREATE INDEX idx_rant_groups_place_id ON rant_groups(place_id);
CREATE INDEX idx_rant_groups_created_at_id ON rant_groups(created_at, id);
CREATE INDEX idx_ratings_landlord_rant_group_id ON ratings_landlord(rant_group_id);
CREATE INDEX idx_ratings_apartment_rant_group_id ON ratings_apartment(rant_group_id);

-- Roommate features
CREATE INDEX idx_roommate_swipes_user_id ON roommate_swipes(user_id);
CREATE INDEX idx_roommate_swipes_target_user_id ON roommate_swipes(target_user_id);
CREATE INDEX idx_roommate_matches_users ON roommate_matches(a_user_id, b_user_id);
CREATE INDEX idx_roommate_messages_match_id_created_at ON roommate_messages(match_id, created_at, id);

-- Roommate ratings
CREATE INDEX idx_ratings_roommate_rater_user_id ON ratings_roommate(rater_user_id);
CREATE INDEX idx_ratings_roommate_ratee_user_id ON ratings_roommate(ratee_user_id);

-- Listings
CREATE INDEX idx_listings_owner_user_id ON listings(owner_user_id);
CREATE INDEX idx_listings_place_id ON listings(place_id);
CREATE INDEX idx_listings_active_created_at_id ON listings(is_active, created_at, id);
CREATE INDEX idx_listing_photos_listing_id_sort ON listing_photos(listing_id, sort);

-- Listing swipes and matches
CREATE INDEX idx_listing_swipes_user_id ON listing_swipes(user_id);
CREATE INDEX idx_listing_swipes_listing_id ON listing_swipes(listing_id);
CREATE INDEX idx_listing_matches_user_id ON listing_matches(user_id);
CREATE INDEX idx_listing_matches_listing_id ON listing_matches(listing_id);

-- Comments
COMMENT ON TABLE users IS 'Application users authenticated via Google OIDC';
COMMENT ON TABLE places IS 'Geographic locations (apartments/buildings) with optional Google Places integration';
COMMENT ON TABLE landlords IS 'Landlord records with hashed phone numbers for PII protection';
COMMENT ON TABLE rant_groups IS 'Combined landlord+apartment rating submissions';
COMMENT ON TABLE ratings_landlord IS 'Landlord-specific scores within a rant group';
COMMENT ON TABLE ratings_apartment IS 'Apartment-specific scores within a rant group';
COMMENT ON TABLE ratings_roommate IS 'Standalone roommate ratings';
COMMENT ON TABLE roommate_swipes IS 'User swipe actions on potential roommates';
COMMENT ON TABLE roommate_matches IS 'Mutual roommate likes resulting in matches';
COMMENT ON TABLE roommate_messages IS 'Messages within roommate matches';
COMMENT ON TABLE listings IS 'Apartment listings created by users';
COMMENT ON TABLE listing_photos IS 'Photos associated with apartment listings';
COMMENT ON TABLE listing_swipes IS 'User swipe actions on apartment listings';
COMMENT ON TABLE listing_matches IS 'Matches between users and apartment listings';