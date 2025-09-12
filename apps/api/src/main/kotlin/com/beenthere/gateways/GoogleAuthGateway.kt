package com.beenthere.gateways

import com.beenthere.accessors.GoogleAuthAccessor
import com.beenthere.accessors.GoogleUserInfo
import org.springframework.stereotype.Component

/**
 * Google Auth Gateway - Domain interface for Google authentication operations
 * Wraps GoogleAuthAccessor and provides domain-specific methods for BeenThere user authentication
 */
@Component
class GoogleAuthGateway(
    private val googleAuthAccessor: GoogleAuthAccessor
) {
    
    /**
     * Authenticate user with Google ID token and return user information
     */
    suspend fun authenticateUser(idToken: String): Result<AuthenticatedUser> {
        return try {
            val googleUserInfo = googleAuthAccessor.verifyIdToken(idToken)
            if (googleUserInfo != null) {
                val authenticatedUser = AuthenticatedUser(
                    googleSub = googleUserInfo.googleSub,
                    email = googleUserInfo.email,
                    emailVerified = googleUserInfo.emailVerified,
                    displayName = googleUserInfo.name ?: "${googleUserInfo.givenName} ${googleUserInfo.familyName}".trim(),
                    photoUrl = googleUserInfo.pictureUrl,
                    givenName = googleUserInfo.givenName,
                    familyName = googleUserInfo.familyName,
                    locale = googleUserInfo.locale
                )
                Result.success(authenticatedUser)
            } else {
                Result.failure(Exception("Invalid Google token"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Google authentication failed: ${e.message}", e))
        }
    }
    
    /**
     * Validate Google ID token without extracting user info
     */
    suspend fun validateToken(idToken: String): Result<Boolean> {
        return try {
            val isValid = googleAuthAccessor.isValidToken(idToken)
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(Exception("Google token validation failed: ${e.message}", e))
        }
    }
    
    /**
     * Extract user info from valid Google ID token
     */
    suspend fun extractUserInfo(idToken: String): Result<GoogleUserInfo?> {
        return try {
            val userInfo = googleAuthAccessor.verifyIdToken(idToken)
            Result.success(userInfo)
        } catch (e: Exception) {
            Result.failure(Exception("Google user info extraction failed: ${e.message}", e))
        }
    }
}

/**
 * Authenticated user data from Google
 */
data class AuthenticatedUser(
    val googleSub: String,
    val email: String,
    val emailVerified: Boolean,
    val displayName: String,
    val photoUrl: String?,
    val givenName: String?,
    val familyName: String?,
    val locale: String?
)