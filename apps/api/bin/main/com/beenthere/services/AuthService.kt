package com.beenthere.services

import com.beenthere.accessors.GoogleAuthAccessor
import com.beenthere.accessors.GoogleUserInfo
import com.beenthere.common.ServiceError
import com.beenthere.dto.auth.GoogleAuthReq
import com.beenthere.dto.auth.GoogleAuthRes
import com.beenthere.dto.auth.UserInfo
import com.beenthere.entities.UserEntity
import com.beenthere.repositories.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Service
class AuthService(
    private val googleAuthAccessor: GoogleAuthAccessor,
    private val userRepository: UserRepository,
    @Value("\${beenthere.jwt.secret}")
    private val jwtSecret: String,
    @Value("\${beenthere.jwt.expiration-hours:24}")
    private val jwtExpirationHours: Long
) {
    
    private val jwtKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    @Transactional
    suspend fun authenticateWithGoogle(req: GoogleAuthReq): Result<GoogleAuthRes> {
        return try {
            // Verify Google ID token and get user info
            val googleUser = googleAuthAccessor.verifyIdToken(req.idToken)
                ?: return Result.failure(ServiceError.InvalidGoogleToken())
            
            // Find or create user
            val user = userRepository.findByGoogleSub(googleUser.googleSub)
                ?: createUser(googleUser)
            
            // Generate JWT
            val jwt = generateJwt(user)
            
            Result.success(GoogleAuthRes(
                jwt = jwt,
                user = UserInfo(
                    id = user.id.toString(),
                    email = user.email,
                    displayName = user.displayName,
                    photoUrl = user.photoUrl,
                    bio = user.bio,
                    hasApartment = user.hasApartment,
                    openToMatching = user.openToMatching,
                    createdAt = user.createdAt.toString()
                )
            ))
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    private suspend fun createUser(googleUser: GoogleUserInfo): UserEntity {
        val newUser = UserEntity(
            email = googleUser.email,
            googleSub = googleUser.googleSub,
            displayName = googleUser.name ?: "Unknown User",
            photoUrl = googleUser.pictureUrl,
            bio = null,
            hasApartment = false,
            openToMatching = false
        )
        return userRepository.save(newUser)
    }
    
    private fun generateJwt(user: UserEntity): String {
        val now = Instant.now()
        val expiration = now.plus(jwtExpirationHours, ChronoUnit.HOURS)
        
        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("displayName", user.displayName)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(jwtKey)
            .compact()
    }
    
    fun verifyJwt(token: String): Result<UUID> {
        return try {
            val claims = Jwts.parser()
                .verifyWith(jwtKey)
                .build()
                .parseSignedClaims(token)
                .payload
            
            val userId = UUID.fromString(claims.subject)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(ServiceError.InvalidGoogleToken())
        }
    }
}