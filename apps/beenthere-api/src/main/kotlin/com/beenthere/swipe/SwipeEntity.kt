package com.beenthere.swipe

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("swipes")
data class SwipeEntity(
    @Id
    val id: String,
    
    @Column("swiper_id")
    val swiperId: String,
    
    @Column("target_type")
    val targetType: String, // "listing" or "user"
    
    @Column("target_id")
    val targetId: String,
    
    @Column("action")
    val action: String, // "like" or "pass"
    
    @Column("created_at")
    val createdAt: Instant = Instant.now()
)
