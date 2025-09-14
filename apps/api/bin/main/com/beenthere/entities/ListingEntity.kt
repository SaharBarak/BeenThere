package com.beenthere.entities

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate
import java.util.*

enum class ListingType(val value: String) {
    ENTIRE_PLACE("ENTIRE_PLACE"),
    ROOMMATE_GROUP("ROOMMATE_GROUP")
}

@Table("listings")
data class ListingEntity(
    @Id
    val id: UUID? = null,
    
    @Column("owner_user_id")
    val ownerUserId: UUID,
    
    @Column("place_id")
    val placeId: UUID,
    
    @Column("title")
    val title: String,
    
    @Column("price")
    val price: Int,
    
    @Column("attrs")
    val attrs: JsonNode? = null,
    
    @Column("auto_accept")
    val autoAccept: Boolean = false,
    
    @Column("is_active")
    val isActive: Boolean = true,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    // Roommate Group Fields (V3 Migration)
    @Column("type")
    val type: String = ListingType.ENTIRE_PLACE.value,
    
    @Column("capacity_total")
    val capacityTotal: Int? = null,
    
    @Column("spots_available")
    val spotsAvailable: Int? = null,
    
    @Column("move_in_date")
    val moveInDate: LocalDate? = null,
    
    @Column("rent_per_room")
    val rentPerRoom: Int? = null
)