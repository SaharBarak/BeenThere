package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("landlords")
data class LandlordEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("phone_hash") val phoneHash: String, // HMAC-SHA256 of E.164 phone
    @Column("created_at") val createdAt: Instant = Instant.now()
)