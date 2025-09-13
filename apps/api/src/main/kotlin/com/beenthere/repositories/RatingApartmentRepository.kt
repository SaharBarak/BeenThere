package com.beenthere.repositories

import com.beenthere.entities.RatingApartmentEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface RatingApartmentRepository : CoroutineCrudRepository<RatingApartmentEntity, UUID> {
    fun findByRantGroupId(rantGroupId: UUID): Flow<RatingApartmentEntity>
    suspend fun findFirstByRantGroupId(rantGroupId: UUID): RatingApartmentEntity?
    suspend fun findByRantGroupIdIn(rantGroupIds: List<UUID>): List<RatingApartmentEntity>
}