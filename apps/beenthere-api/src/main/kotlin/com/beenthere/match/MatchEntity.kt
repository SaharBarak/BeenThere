package com.beenthere.match

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("matches")
data class MatchEntity(
    @Id
    val id: String,
    
    @Column("user_id")
    val userId: String,
    
    @Column("landlord_id")
    val landlordId: String,
    
    @Column("listing_id")
    val listingId: String,
    
    @Column("status")
    val status: String,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)

enum class MatchStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    EXPIRED
}
