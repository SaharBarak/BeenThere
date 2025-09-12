package com.beenthere.repositories

import com.beenthere.entities.LandlordEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LandlordRepository : CoroutineCrudRepository<LandlordEntity, UUID> {
    suspend fun findByPhoneHash(phoneHash: String): LandlordEntity?
}