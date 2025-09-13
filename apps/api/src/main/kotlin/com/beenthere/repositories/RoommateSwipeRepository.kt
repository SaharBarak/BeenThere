package com.beenthere.repositories

import com.beenthere.entities.RoommateSwipeEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface RoommateSwipeRepository : CoroutineCrudRepository<RoommateSwipeEntity, UUID>, CoroutineSortingRepository<RoommateSwipeEntity, UUID> {
    
    suspend fun findByUserIdAndTargetUserId(userId: UUID, targetUserId: UUID): RoommateSwipeEntity?
    
    fun findByUserIdOrderByCreatedAtDesc(userId: UUID): Flow<RoommateSwipeEntity>
    
    fun findByUserId(userId: UUID): Flow<RoommateSwipeEntity>
    
    @Query("""
        SELECT COUNT(*) FROM roommate_swipes 
        WHERE user_id = :userId AND target_user_id = :targetUserId AND action = 'LIKE'
    """)
    suspend fun countLikesByUserIdAndTargetUserId(userId: UUID, targetUserId: UUID): Long
    
    @Query("""
        SELECT COUNT(*) FROM roommate_swipes rs1
        WHERE rs1.user_id = :userId 
        AND rs1.target_user_id = :targetUserId 
        AND rs1.action = 'LIKE'
        AND EXISTS (
            SELECT 1 FROM roommate_swipes rs2
            WHERE rs2.user_id = :targetUserId 
            AND rs2.target_user_id = :userId 
            AND rs2.action = 'LIKE'
        )
    """)
    suspend fun countMutualLikes(userId: UUID, targetUserId: UUID): Long
}