package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("roommate_swipes")
data class RoommateSwipeEntity(
    @Id
    val id: UUID? = null,
    
    @Column("user_id")
    val userId: UUID,
    
    @Column("target_user_id")
    val targetUserId: UUID,
    
    @Column("action")
    val action: String, // 'LIKE' | 'PASS'
    
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)