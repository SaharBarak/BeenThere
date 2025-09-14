package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("roommate_messages")
data class RoommateMessageEntity(
    @Id
    val id: UUID? = null,
    
    @Column("match_id")
    val matchId: UUID,
    
    @Column("sender_user_id")
    val senderUserId: UUID,
    
    @Column("body")
    val body: String,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)