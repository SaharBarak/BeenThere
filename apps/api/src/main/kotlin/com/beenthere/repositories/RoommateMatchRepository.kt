package com.beenthere.repositories

import com.beenthere.entities.RoommateMatchEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface RoommateMatchRepository : CoroutineCrudRepository<RoommateMatchEntity, UUID>, CoroutineSortingRepository<RoommateMatchEntity, UUID> {
    
    @Query("""
        SELECT * FROM roommate_matches 
        WHERE (a_user_id = :userId OR b_user_id = :userId) 
        ORDER BY created_at DESC
    """)
    fun findMatchesForUser(userId: UUID): Flow<RoommateMatchEntity>
    
    fun findByUserId(userId: UUID): Flow<RoommateMatchEntity> = findMatchesForUser(userId)
    
    @Query("""
        SELECT * FROM roommate_matches 
        WHERE (a_user_id = :userId1 AND b_user_id = :userId2) 
           OR (a_user_id = :userId2 AND b_user_id = :userId1)
    """)
    suspend fun findMatchBetweenUsers(userId1: UUID, userId2: UUID): RoommateMatchEntity?
}