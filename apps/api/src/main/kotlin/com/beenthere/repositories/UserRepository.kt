package com.beenthere.repositories

import com.beenthere.entities.UserEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface UserRepository : CoroutineCrudRepository<UserEntity, UUID>, CoroutineSortingRepository<UserEntity, UUID> {
    suspend fun findByGoogleSub(googleSub: String): UserEntity?
    suspend fun findByEmail(email: String): UserEntity?
    
    @Query("""
        SELECT * FROM users 
        WHERE id != :currentUserId 
        AND open_to_matching = true
        AND id NOT IN (:excludedUserIds)
        ORDER BY created_at DESC, id DESC
        LIMIT :limit
    """)
    fun findPotentialRoommates(currentUserId: UUID, excludedUserIds: List<UUID>, limit: Int): Flow<UserEntity>
    
    @Query("""
        SELECT * FROM users 
        WHERE id != :currentUserId 
        AND open_to_matching = true
        AND id NOT IN (:excludedUserIds)
        AND created_at < :cursor
        ORDER BY created_at DESC, id DESC
        LIMIT :limit
    """)
    fun findPotentialRoommatesAfterCursor(currentUserId: UUID, excludedUserIds: List<UUID>, cursor: Instant, limit: Int): Flow<UserEntity>
}