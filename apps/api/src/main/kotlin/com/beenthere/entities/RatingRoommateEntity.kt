package com.beenthere.entities

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("ratings_roommate")
data class RatingRoommateEntity(
    @Id
    val id: UUID? = null,
    
    @Column("rater_user_id")
    val raterUserId: UUID,
    
    @Column("ratee_user_id")
    val rateeUserId: UUID? = null,
    
    @Column("ratee_hint")
    val rateeHint: JsonNode? = null,
    
    @Column("scores")
    val scores: JsonNode,
    
    @Column("comment")
    val comment: String? = null,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)