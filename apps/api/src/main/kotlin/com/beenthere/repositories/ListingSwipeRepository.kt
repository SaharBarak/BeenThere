package com.beenthere.repositories

import com.beenthere.entities.ListingSwipeEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface ListingSwipeRepository : CoroutineCrudRepository<ListingSwipeEntity, UUID>, CoroutineSortingRepository<ListingSwipeEntity, UUID> {
    
    suspend fun findByUserIdAndListingId(userId: UUID, listingId: UUID): ListingSwipeEntity?
    
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): Flow<ListingSwipeEntity>
    
    fun findByListingIdOrderByCreatedAtDesc(listingId: UUID): Flow<ListingSwipeEntity>
    
    @Query("""
        SELECT COUNT(*) FROM listing_swipes 
        WHERE listing_id = :listingId AND action = 'LIKE'
    """)
    suspend fun countLikesForListing(listingId: UUID): Long
    
    @Query("""
        SELECT ls.* FROM listing_swipes ls
        INNER JOIN listings l ON ls.listing_id = l.id
        WHERE l.owner_user_id = :ownerId AND ls.action = 'LIKE'
        ORDER BY ls.created_at DESC
    """)
    fun findLikesForOwnerListings(ownerId: UUID): Flow<ListingSwipeEntity>
}