package com.beenthere.repositories

import com.beenthere.entities.RoommateMessageEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface RoommateMessageRepository : CoroutineCrudRepository<RoommateMessageEntity, UUID>, CoroutineSortingRepository<RoommateMessageEntity, UUID> {
    
    fun findByMatchIdOrderByCreatedAtDesc(matchId: UUID): Flow<RoommateMessageEntity>
    
    @Query("""
        SELECT * FROM roommate_messages 
        WHERE match_id = :matchId 
        AND (created_at < :cursor OR (created_at = :cursor AND id < :id))
        ORDER BY created_at DESC, id DESC 
        LIMIT :limit
    """)
    fun findByMatchIdWithSeekPagination(
        matchId: UUID,
        cursor: Instant,
        id: UUID,
        limit: Int
    ): Flow<RoommateMessageEntity>
    
    @Query("""
        SELECT * FROM roommate_messages 
        WHERE match_id = :matchId 
        ORDER BY created_at DESC, id DESC 
        LIMIT :limit
    """)
    fun findByMatchIdWithLimitDesc(matchId: UUID, limit: Int): Flow<RoommateMessageEntity>
    
    fun findByMatchIdOrderByCreatedAtDesc(matchId: UUID, limit: Int): Flow<RoommateMessageEntity> = 
        findByMatchIdWithLimitDesc(matchId, limit)
        
    @Query("""
        SELECT * FROM roommate_messages 
        WHERE match_id = :matchId
        AND created_at < :cursor
        ORDER BY created_at DESC, id DESC 
        LIMIT :limit
    """)
    fun findByMatchIdBeforeCursor(matchId: UUID, cursor: Instant, limit: Int): Flow<RoommateMessageEntity>
    
    @Query("""
        SELECT * FROM roommate_messages 
        WHERE match_id = :matchId 
        ORDER BY created_at DESC 
        LIMIT 1
    """)
    fun findLastMessageForMatch(matchId: UUID): Flow<RoommateMessageEntity>
}