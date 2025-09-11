package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("ratings_roommate")
data class RatingRoommateEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("rater_user_id") val raterUserId: UUID,
    @Column("ratee_user_id") val rateeUserId: UUID? = null,
    @Column("ratee_hint") val rateeHint: String? = null, // JSON: {"name":"שם", "org":"חברה"}
    @Column("scores") val scores: String, // JSON: {"cleanliness":8, "communication":9, "reliability":7, "respect":8, "costSharing":6}
    @Column("comment") val comment: String? = null,
    @Column("created_at") val createdAt: Instant = Instant.now()
)