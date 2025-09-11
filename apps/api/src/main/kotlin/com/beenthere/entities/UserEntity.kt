package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("users")
data class UserEntity(
    @Id val id: UUID = UUID.randomUUID(),
    @Column("email") val email: String,
    @Column("google_sub") val googleSub: String,
    @Column("display_name") val displayName: String,
    @Column("photo_url") val photoUrl: String? = null,
    @Column("bio") val bio: String? = null,
    @Column("has_apartment") val hasApartment: Boolean = false,
    @Column("open_to_matching") val openToMatching: Boolean = true,
    @Column("created_at") val createdAt: Instant = Instant.now()
)