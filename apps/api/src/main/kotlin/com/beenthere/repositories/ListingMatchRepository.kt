package com.beenthere.repositories

import com.beenthere.entities.ListingMatchEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface ListingMatchRepository : CoroutineCrudRepository<ListingMatchEntity, UUID>, CoroutineSortingRepository<ListingMatchEntity, UUID> {
    
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): Flow<ListingMatchEntity>
    
    fun findByListingIdOrderByCreatedAtDesc(listingId: UUID): Flow<ListingMatchEntity>
    
    suspend fun findByUserIdAndListingId(userId: UUID, listingId: UUID): ListingMatchEntity?
    
    @Query("""
        SELECT lm.* FROM listing_matches lm
        INNER JOIN listings l ON lm.listing_id = l.id
        WHERE l.owner_user_id = :ownerId
        ORDER BY lm.created_at DESC
    """)
    fun findMatchesForOwnerListings(ownerId: UUID): Flow<ListingMatchEntity>
}