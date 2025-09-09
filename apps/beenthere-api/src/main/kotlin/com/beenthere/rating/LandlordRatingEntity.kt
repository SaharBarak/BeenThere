package com.beenthere.rating

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("landlord_ratings")
data class LandlordRatingEntity(
    @Id
    val id: String,
    
    @Column("rater_id")
    val raterId: String,
    
    @Column("landlord_id")
    val landlordId: String,
    
    @Column("rating")
    val rating: Int, // 1-5 stars
    
    @Column("comment")
    val comment: String? = null,
    
    @Column("responsiveness_rating")
    val responsivenessRating: Int? = null,
    
    @Column("maintenance_rating")
    val maintenanceRating: Int? = null,
    
    @Column("communication_rating")
    val communicationRating: Int? = null,
    
    @Column("fairness_rating")
    val fairnessRating: Int? = null,
    
    @Column("professionalism_rating")
    val professionalismRating: Int? = null,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)
