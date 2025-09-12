package com.beenthere.entities

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("ratings_landlord")
data class RatingLandlordEntity(
    @Id
    val id: UUID? = null,
    
    @Column("rant_group_id")
    val rantGroupId: UUID,
    
    @Column("scores")
    val scores: JsonNode
)