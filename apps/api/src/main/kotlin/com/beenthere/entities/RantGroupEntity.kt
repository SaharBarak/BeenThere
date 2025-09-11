package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Table("rant_groups")
data class RantGroupEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("rater_user_id") val raterUserId: UUID,
    @Column("landlord_id") val landlordId: UUID,
    @Column("place_id") val placeId: UUID,
    @Column("period_start") val periodStart: LocalDate? = null,
    @Column("period_end") val periodEnd: LocalDate? = null,
    @Column("is_current_residence") val isCurrentResidence: Boolean = false,
    @Column("comment") val comment: String? = null,
    @Column("created_at") val createdAt: Instant = Instant.now()
)