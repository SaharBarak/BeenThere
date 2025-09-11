package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("ratings_landlord")
data class RatingLandlordEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("rant_group_id") val rantGroupId: UUID,
    @Column("scores") val scores: String // JSON string: {"fairness":8, "response":7, "maintenance":9, "privacy":6}
)