-- BeenThere Database Schema
-- This migration creates all the necessary tables for the BeenThere application

-- Users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    bio TEXT,
    profile_image_url VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Listings table
CREATE TABLE listings (
    id VARCHAR(36) PRIMARY KEY,
    landlord_id VARCHAR(36) NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(10) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    rent_amount DECIMAL(10, 2) NOT NULL,
    deposit_amount DECIMAL(10, 2),
    bedrooms INTEGER NOT NULL,
    bathrooms DECIMAL(3, 1) NOT NULL,
    square_feet INTEGER,
    property_type VARCHAR(20) NOT NULL CHECK (property_type IN ('APARTMENT', 'HOUSE', 'CONDO', 'TOWNHOUSE', 'STUDIO', 'ROOM')),
    furnished BOOLEAN DEFAULT FALSE,
    pet_friendly BOOLEAN DEFAULT FALSE,
    smoking_allowed BOOLEAN DEFAULT FALSE,
    utilities_included BOOLEAN DEFAULT FALSE,
    parking_available BOOLEAN DEFAULT FALSE,
    laundry_available BOOLEAN DEFAULT FALSE,
    gym_available BOOLEAN DEFAULT FALSE,
    pool_available BOOLEAN DEFAULT FALSE,
    available_date DATE NOT NULL,
    lease_duration_months INTEGER,
    images TEXT[], -- Array of image URLs
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Swipes table
CREATE TABLE swipes (
    id VARCHAR(36) PRIMARY KEY,
    swiper_id VARCHAR(36) NOT NULL REFERENCES users(id),
    target_type VARCHAR(50) NOT NULL, -- 'listing' or 'user'
    target_id VARCHAR(36) NOT NULL,
    action VARCHAR(50) NOT NULL, -- 'like' or 'pass'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(swiper_id, target_type, target_id) -- A user can only swipe on a target once
);

-- Matches table
CREATE TABLE matches (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    landlord_id VARCHAR(36) NOT NULL REFERENCES users(id),
    listing_id VARCHAR(36) NOT NULL REFERENCES listings(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, landlord_id, listing_id) -- Prevent duplicate matches
);

-- House ratings table
CREATE TABLE house_ratings (
    id VARCHAR(36) PRIMARY KEY,
    rater_id VARCHAR(36) NOT NULL REFERENCES users(id),
    listing_id VARCHAR(36) NOT NULL REFERENCES listings(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    cleanliness_rating INTEGER CHECK (cleanliness_rating >= 1 AND cleanliness_rating <= 5),
    location_rating INTEGER CHECK (location_rating >= 1 AND location_rating <= 5),
    value_rating INTEGER CHECK (value_rating >= 1 AND value_rating <= 5),
    amenities_rating INTEGER CHECK (amenities_rating >= 1 AND amenities_rating <= 5),
    noise_level_rating INTEGER CHECK (noise_level_rating >= 1 AND noise_level_rating <= 5),
    safety_rating INTEGER CHECK (safety_rating >= 1 AND safety_rating <= 5),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(rater_id, listing_id) -- Prevent duplicate ratings
);

-- Roommate ratings table
CREATE TABLE roommate_ratings (
    id VARCHAR(36) PRIMARY KEY,
    rater_id VARCHAR(36) NOT NULL REFERENCES users(id),
    rated_user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    cleanliness_rating INTEGER CHECK (cleanliness_rating >= 1 AND cleanliness_rating <= 5),
    communication_rating INTEGER CHECK (communication_rating >= 1 AND communication_rating <= 5),
    respect_rating INTEGER CHECK (respect_rating >= 1 AND respect_rating <= 5),
    reliability_rating INTEGER CHECK (reliability_rating >= 1 AND reliability_rating <= 5),
    social_rating INTEGER CHECK (social_rating >= 1 AND social_rating <= 5),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(rater_id, rated_user_id) -- Prevent duplicate ratings
);

-- Landlord ratings table
CREATE TABLE landlord_ratings (
    id VARCHAR(36) PRIMARY KEY,
    rater_id VARCHAR(36) NOT NULL REFERENCES users(id),
    landlord_id VARCHAR(36) NOT NULL REFERENCES users(id),
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    responsiveness_rating INTEGER CHECK (responsiveness_rating >= 1 AND responsiveness_rating <= 5),
    maintenance_rating INTEGER CHECK (maintenance_rating >= 1 AND maintenance_rating <= 5),
    communication_rating INTEGER CHECK (communication_rating >= 1 AND communication_rating <= 5),
    fairness_rating INTEGER CHECK (fairness_rating >= 1 AND fairness_rating <= 5),
    professionalism_rating INTEGER CHECK (professionalism_rating >= 1 AND professionalism_rating <= 5),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(rater_id, landlord_id) -- Prevent duplicate ratings
);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active);

CREATE INDEX idx_listings_landlord ON listings(landlord_id);
CREATE INDEX idx_listings_city_state ON listings(city, state);
CREATE INDEX idx_listings_rent_amount ON listings(rent_amount);
CREATE INDEX idx_listings_bedrooms ON listings(bedrooms);
CREATE INDEX idx_listings_property_type ON listings(property_type);
CREATE INDEX idx_listings_active ON listings(is_active);

CREATE INDEX idx_swipes_user ON swipes(user_id);
CREATE INDEX idx_swipes_listing ON swipes(listing_id);

CREATE INDEX idx_matches_user ON matches(user_id);
CREATE INDEX idx_matches_landlord ON matches(landlord_id);
CREATE INDEX idx_matches_status ON matches(status);

CREATE INDEX idx_house_ratings_listing ON house_ratings(listing_id);
CREATE INDEX idx_house_ratings_rater ON house_ratings(rater_id);

CREATE INDEX idx_roommate_ratings_rated_user ON roommate_ratings(rated_user_id);
CREATE INDEX idx_roommate_ratings_rater ON roommate_ratings(rater_id);

CREATE INDEX idx_landlord_ratings_landlord ON landlord_ratings(landlord_id);
CREATE INDEX idx_landlord_ratings_rater ON landlord_ratings(rater_id);

-- Subscriptions Table
CREATE TABLE subscriptions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL REFERENCES users(id),
    plan_id VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'active',
    period_end TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_subscriptions_user_id ON subscriptions (user_id);
CREATE INDEX idx_subscriptions_status ON subscriptions (status);
