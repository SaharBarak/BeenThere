package com.beenthere.rating

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("roommate_ratings")
data class RoommateRatingEntity(
    @Id
    val id: String,
    
    @Column("rater_id")
    val raterId: String,
    
    @Column("rated_user_id")
    val ratedUserId: String,
    
    @Column("rating")
    val rating: Int, // 1-5 stars
    
    @Column("comment")
    val comment: String? = null,
    
    @Column("cleanliness_rating")
    val cleanlinessRating: Int? = null,
    
    @Column("communication_rating")
    val communicationRating: Int? = null,
    
    @Column("respect_rating")
    val respectRating: Int? = null,
    
    @Column("reliability_rating")
    val reliabilityRating: Int? = null,
    
    @Column("social_rating")
    val socialRating: Int? = null,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)
