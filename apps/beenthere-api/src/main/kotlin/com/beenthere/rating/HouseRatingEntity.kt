package com.beenthere.rating

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant

@Table("house_ratings")
data class HouseRatingEntity(
    @Id
    val id: String,
    
    @Column("rater_id")
    val raterId: String,
    
    @Column("listing_id")
    val listingId: String,
    
    @Column("rating")
    val rating: Int, // 1-5 stars
    
    @Column("comment")
    val comment: String? = null,
    
    @Column("cleanliness_rating")
    val cleanlinessRating: Int? = null,
    
    @Column("location_rating")
    val locationRating: Int? = null,
    
    @Column("value_rating")
    val valueRating: Int? = null,
    
    @Column("amenities_rating")
    val amenitiesRating: Int? = null,
    
    @Column("noise_level_rating")
    val noiseLevelRating: Int? = null,
    
    @Column("safety_rating")
    val safetyRating: Int? = null,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)
