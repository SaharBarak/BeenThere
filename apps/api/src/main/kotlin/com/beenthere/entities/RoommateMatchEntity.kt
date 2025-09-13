package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("roommate_matches")
data class RoommateMatchEntity(
    @Id
    val id: UUID? = null,
    
    @Column("a_user_id")
    val aUserId: UUID,
    
    @Column("b_user_id")
    val bUserId: UUID,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)