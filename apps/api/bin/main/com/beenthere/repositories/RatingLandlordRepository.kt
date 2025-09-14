package com.beenthere.repositories

import com.beenthere.entities.RatingLandlordEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface RatingLandlordRepository : CoroutineCrudRepository<RatingLandlordEntity, UUID> {
    fun findByRantGroupId(rantGroupId: UUID): Flow<RatingLandlordEntity>
    suspend fun findFirstByRantGroupId(rantGroupId: UUID): RatingLandlordEntity?
    suspend fun findByRantGroupIdIn(rantGroupIds: List<UUID>): List<RatingLandlordEntity>
}