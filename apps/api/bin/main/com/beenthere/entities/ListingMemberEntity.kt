package com.beenthere.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

enum class MemberRole(val value: String) {
    OWNER("OWNER"),
    TENANT("TENANT")
}

@Table("listing_members")
data class ListingMemberEntity(
    @Id
    val id: UUID? = null,
    
    @Column("listing_id")
    val listingId: UUID,
    
    @Column("user_id")
    val userId: UUID,
    
    @Column("is_current")
    val isCurrent: Boolean = true,
    
    @Column("joined_at")
    val joinedAt: Instant = Instant.now(),
    
    @Column("left_at")
    val leftAt: Instant? = null,
    
    // V3 Migration fields
    @Column("role")
    val role: String = MemberRole.TENANT.value,
    
    @Column("display_order")
    val displayOrder: Int = 0
)