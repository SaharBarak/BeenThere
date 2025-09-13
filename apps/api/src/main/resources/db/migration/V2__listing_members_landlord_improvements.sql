-- BeenThere Database Schema V2
-- Enhanced features: listing_members, landlord_id references, and description fields

-- Add description field to listings
ALTER TABLE listings ADD COLUMN description TEXT CHECK (length(description) <= 1000);

-- Create listing_members table for roommate associations
CREATE TABLE listing_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    listing_id UUID NOT NULL REFERENCES listings(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_current BOOLEAN NOT NULL DEFAULT true,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    left_at TIMESTAMPTZ,
    CONSTRAINT listing_members_unique UNIQUE (listing_id, user_id),
    CONSTRAINT listing_members_dates_check CHECK (left_at IS NULL OR left_at > joined_at)
);

-- Add landlord_id reference to listings for direct landlord association
ALTER TABLE listings ADD COLUMN landlord_id UUID REFERENCES landlords(id);

-- Create indexes for performance
CREATE INDEX idx_listing_members_listing_id ON listing_members(listing_id);
CREATE INDEX idx_listing_members_user_id ON listing_members(user_id);
CREATE INDEX idx_listing_members_is_current ON listing_members(is_current) WHERE is_current = true;
CREATE INDEX idx_listings_landlord_id ON listings(landlord_id);

-- Add indexes for rating aggregations (needed for W3 feed enrichment)
CREATE INDEX idx_ratings_apartment_place_id ON ratings_apartment(rant_group_id);
CREATE INDEX idx_ratings_landlord_rant_group_id ON ratings_landlord(rant_group_id);
CREATE INDEX idx_rant_groups_place_id ON rant_groups(place_id);
CREATE INDEX idx_rant_groups_landlord_id ON rant_groups(landlord_id);

-- Add indexes for roommate ratings aggregation
CREATE INDEX idx_ratings_roommate_ratee_user_id ON ratings_roommate(ratee_user_id) WHERE ratee_user_id IS NOT NULL;

-- Performance indexes for feeds and pagination
CREATE INDEX idx_listings_created_at_id ON listings(created_at DESC, id DESC) WHERE is_active = true;
CREATE INDEX idx_users_created_at ON users(created_at DESC);
CREATE INDEX idx_roommate_matches_users ON roommate_matches(a_user_id, b_user_id);
CREATE INDEX idx_roommate_matches_created_at ON roommate_matches(created_at DESC);

-- Add constraint to ensure listing owner is not in listing_members
-- (Owner should be implicit, not explicit in members table)
CREATE OR REPLACE FUNCTION check_owner_not_member()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM listings 
        WHERE id = NEW.listing_id 
        AND owner_user_id = NEW.user_id
    ) THEN
        RAISE EXCEPTION 'Listing owner cannot be explicitly added as a member';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_check_owner_not_member
    BEFORE INSERT OR UPDATE ON listing_members
    FOR EACH ROW
    EXECUTE FUNCTION check_owner_not_member();

-- Enhance places table with better search capabilities
CREATE INDEX idx_places_google_place_id ON places(google_place_id);
CREATE INDEX idx_places_location ON places(lat, lng) WHERE lat IS NOT NULL AND lng IS NOT NULL;