package com.beenthere.repositories

import com.beenthere.entities.ListingEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface ListingRepository : CoroutineCrudRepository<ListingEntity, UUID>, CoroutineSortingRepository<ListingEntity, UUID> {
    
    fun findByOwnerUserIdOrderByCreatedAtDesc(ownerUserId: UUID): Flow<ListingEntity>
    
    fun findByIsActiveTrueOrderByCreatedAtDesc(): Flow<ListingEntity>
    
    @Query("""
        SELECT * FROM listings 
        WHERE is_active = true 
        AND (created_at < :cursor OR (created_at = :cursor AND id < :id))
        ORDER BY created_at DESC, id DESC 
        LIMIT :limit
    """)
    fun findActiveWithSeekPagination(
        cursor: Instant,
        id: UUID,
        limit: Int
    ): Flow<ListingEntity>
    
    @Query("""
        SELECT * FROM listings 
        WHERE is_active = true 
        ORDER BY created_at DESC, id DESC 
        LIMIT :limit
    """)
    fun findActiveWithLimitDesc(limit: Int): Flow<ListingEntity>
    
    suspend fun findByIdAndOwnerUserId(id: UUID, ownerUserId: UUID): ListingEntity?
    
    fun findActiveListings(limit: Int): Flow<ListingEntity> = findActiveWithLimitDesc(limit)
    
    @Query("""
        SELECT * FROM listings 
        WHERE is_active = true 
        AND created_at < :cursor
        ORDER BY created_at DESC, id DESC 
        LIMIT :limit
    """)
    fun findActiveListingsAfterCursor(cursor: Instant, limit: Int): Flow<ListingEntity>
}