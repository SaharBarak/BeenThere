package com.beenthere.repositories

import com.beenthere.entities.RantGroupEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface RantGroupRepository : CoroutineCrudRepository<RantGroupEntity, UUID>, CoroutineSortingRepository<RantGroupEntity, UUID> {
    
    fun findByPlaceIdOrderByCreatedAtDesc(placeId: UUID): Flow<RantGroupEntity>
    
    fun findByRaterUserIdOrderByCreatedAtDesc(raterUserId: UUID): Flow<RantGroupEntity>
    
    suspend fun findByPlaceId(placeId: UUID): List<RantGroupEntity>
    
    @Query("""
        SELECT * FROM rant_groups 
        WHERE place_id = :placeId 
        ORDER BY created_at DESC, id DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun findByPlaceIdWithPagination(placeId: UUID, limit: Int, offset: Int): Flow<RantGroupEntity>
}