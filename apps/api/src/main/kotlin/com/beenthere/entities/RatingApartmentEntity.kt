package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("ratings_apartment")
data class RatingApartmentEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("rant_group_id") val rantGroupId: UUID,
    @Column("scores") val scores: String, // JSON: {"condition":7, "noise":5, "utilities":8, "sunlightMold":6}
    @Column("extras") val extras: String? = null // JSON: optional extra ratings
)