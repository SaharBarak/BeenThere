package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("listing_matches")
data class ListingMatchEntity(
    @Id
    val id: UUID? = null,
    
    @Column("user_id")
    val userId: UUID,
    
    @Column("listing_id")
    val listingId: UUID,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)