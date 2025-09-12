package com.beenthere.repositories

import com.beenthere.entities.RatingRoommateEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import kotlinx.coroutines.flow.Flow

@Repository
interface RatingRoommateRepository : CoroutineCrudRepository<RatingRoommateEntity, UUID>, CoroutineSortingRepository<RatingRoommateEntity, UUID> {
    fun findByRaterUserIdOrderByCreatedAtDesc(raterUserId: UUID): Flow<RatingRoommateEntity>
    fun findByRateeUserIdOrderByCreatedAtDesc(rateeUserId: UUID): Flow<RatingRoommateEntity>
}