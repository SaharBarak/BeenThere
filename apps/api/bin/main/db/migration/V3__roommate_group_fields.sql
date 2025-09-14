-- BeenThere Database Schema V3
-- Roommate Group Upgrade: Add fields for two-sided roommate matching

-- Add roommate group fields to listings table
ALTER TABLE listings 
    ADD COLUMN IF NOT EXISTS type TEXT DEFAULT 'ENTIRE_PLACE' CHECK (type IN ('ENTIRE_PLACE', 'ROOMMATE_GROUP')),
    ADD COLUMN IF NOT EXISTS capacity_total INT,
    ADD COLUMN IF NOT EXISTS spots_available INT,
    ADD COLUMN IF NOT EXISTS move_in_date DATE,
    ADD COLUMN IF NOT EXISTS rent_per_room INT;

-- Create indexes for roommate group filtering and performance
CREATE INDEX IF NOT EXISTS idx_listings_type_active ON listings(type, is_active, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_listing_members_current ON listing_members(listing_id) WHERE is_current = true;
CREATE INDEX IF NOT EXISTS idx_listings_spots_available ON listings(spots_available) WHERE spots_available > 0 AND type = 'ROOMMATE_GROUP';

-- Add role field to listing_members if not exists (for OWNER vs TENANT distinction)
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'listing_members' AND column_name = 'role') THEN
        ALTER TABLE listing_members ADD COLUMN role TEXT DEFAULT 'TENANT' CHECK (role IN ('OWNER', 'TENANT'));
    END IF;
END $$;

-- Add display_order for member bubbles ordering
ALTER TABLE listing_members ADD COLUMN IF NOT EXISTS display_order INT DEFAULT 0;

-- Update existing listings to have default type
UPDATE listings SET type = 'ENTIRE_PLACE' WHERE type IS NULL;

-- Add constraint to ensure spots_available <= capacity_total for roommate groups
ALTER TABLE listings ADD CONSTRAINT check_roommate_group_capacity 
    CHECK (type != 'ROOMMATE_GROUP' OR (capacity_total IS NOT NULL AND spots_available IS NOT NULL AND spots_available <= capacity_total));

-- Comments for documentation
COMMENT ON COLUMN listings.type IS 'Type of listing: ENTIRE_PLACE for regular rentals, ROOMMATE_GROUP for shared apartments';
COMMENT ON COLUMN listings.capacity_total IS 'Total number of beds/rooms in the apartment (for ROOMMATE_GROUP)';
COMMENT ON COLUMN listings.spots_available IS 'Number of spots currently available for new roommates (for ROOMMATE_GROUP)';
COMMENT ON COLUMN listings.move_in_date IS 'Preferred move-in date for ROOMMATE_GROUP listings';
COMMENT ON COLUMN listings.rent_per_room IS 'Rent per room/spot for ROOMMATE_GROUP listings';
COMMENT ON COLUMN listing_members.role IS 'Role of the member: OWNER (lease holder) or TENANT (regular roommate)';
COMMENT ON COLUMN listing_members.display_order IS 'Order for displaying member bubbles on cards (0 = first)';

-- Indexes for performance queries from upgrade_roommate_task.md
CREATE INDEX IF NOT EXISTS idx_listing_members_role ON listing_members(role, listing_id);
CREATE INDEX IF NOT EXISTS idx_listings_move_in_date ON listings(move_in_date) WHERE type = 'ROOMMATE_GROUP';