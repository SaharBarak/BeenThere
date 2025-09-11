package com.beenthere.repositories

import com.beenthere.entities.LandlordEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LandlordRepository : CoroutineCrudRepository<LandlordEntity, UUID> {
    
    /**
     * Find landlord by phone hash.
     * Used to check if landlord already exists before creating new one.
     */
    suspend fun findByPhoneHash(phoneHash: String): LandlordEntity?
}