package com.beenthere.repositories

import com.beenthere.entities.ListingMemberEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ListingMemberRepository : CoroutineCrudRepository<ListingMemberEntity, UUID> {
    
    /**
     * Find all current members for a listing, ordered by display_order
     */
    @Query("""
        SELECT * FROM listing_members 
        WHERE listing_id = :listingId AND is_current = true 
        ORDER BY display_order ASC, joined_at ASC
    """)
    suspend fun findCurrentMembersByListingId(listingId: UUID): List<ListingMemberEntity>
    
    /**
     * Find all members (current and past) for a listing
     */
    suspend fun findByListingId(listingId: UUID): List<ListingMemberEntity>
    
    /**
     * Find current membership for a user in a specific listing
     */
    @Query("""
        SELECT * FROM listing_members 
        WHERE listing_id = :listingId AND user_id = :userId AND is_current = true
    """)
    suspend fun findCurrentMembership(listingId: UUID, userId: UUID): ListingMemberEntity?
    
    /**
     * Check if user is a current member (owner or tenant) of a listing
     */
    @Query("""
        SELECT COUNT(*) > 0 FROM listing_members 
        WHERE listing_id = :listingId AND user_id = :userId AND is_current = true
    """)
    suspend fun isCurrentMember(listingId: UUID, userId: UUID): Boolean
    
    /**
     * Check if user is the owner of a listing
     */
    @Query("""
        SELECT COUNT(*) > 0 FROM listing_members 
        WHERE listing_id = :listingId AND user_id = :userId AND role = 'OWNER' AND is_current = true
    """)
    suspend fun isOwner(listingId: UUID, userId: UUID): Boolean
    
    /**
     * Get member count for multiple listings (for feed enrichment)
     */
    @Query("""
        SELECT listing_id, COUNT(*) as member_count 
        FROM listing_members 
        WHERE listing_id = ANY(:listingIds) AND is_current = true
        GROUP BY listing_id
    """)
    suspend fun getMemberCounts(listingIds: List<UUID>): List<MemberCountProjection>
    
    /**
     * Find current members by multiple listing IDs (for feed bubbles)
     */
    @Query("""
        SELECT lm.*, u.display_name, u.photo_url
        FROM listing_members lm
        JOIN users u ON lm.user_id = u.id
        WHERE lm.listing_id = ANY(:listingIds) AND lm.is_current = true
        ORDER BY lm.listing_id, lm.display_order ASC, lm.joined_at ASC
        LIMIT 100
    """)
    suspend fun findCurrentMembersWithUserInfo(listingIds: List<UUID>): List<MemberWithUserProjection>
}

/**
 * Projection for member count queries
 */
interface MemberCountProjection {
    val listingId: UUID
    val memberCount: Int
}

/**
 * Projection for member with user info queries
 */
interface MemberWithUserProjection {
    val id: UUID
    val listingId: UUID
    val userId: UUID
    val role: String
    val displayOrder: Int
    val displayName: String
    val photoUrl: String?
}