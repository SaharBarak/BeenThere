package com.beenthere.repositories

import com.beenthere.entities.UserEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CoroutineCrudRepository<UserEntity, UUID> {
    
    /**
     * Find user by Google subject ID.
     * Used for authentication and user lookup.
     */
    suspend fun findByGoogleSub(googleSub: String): UserEntity?
    
    /**
     * Find user by email.
     * Used for user lookup and validation.
     */
    suspend fun findByEmail(email: String): UserEntity?
}