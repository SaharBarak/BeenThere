package com.beenthere.entities

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("places")
data class PlaceEntity(
    @Id
    val id: UUID? = null,
    
    @Column("google_place_id")
    val googlePlaceId: String? = null,
    
    @Column("formatted_address")
    val formattedAddress: String? = null,
    
    @Column("lat")
    val lat: Double? = null,
    
    @Column("lng")
    val lng: Double? = null,
    
    @Column("attrs")
    val attrs: JsonNode? = null,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)