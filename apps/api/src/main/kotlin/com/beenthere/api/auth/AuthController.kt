package com.beenthere.api.auth

import com.beenthere.common.toResponseEntity
import com.github.michaelbull.result.Ok
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Authentication endpoints for Google OAuth integration.
 * MVP: Stub implementation returning mock data.
 */
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    
    /**
     * Google OAuth authentication endpoint.
     * Validates Google ID token and returns JWT + user profile.
     */
    @PostMapping("/google")
    fun authenticateWithGoogle(@RequestBody request: Map<String, Any>): ResponseEntity<*> {
        // TODO: Implement Google ID token validation
        // TODO: Create or update user in database
        // TODO: Generate JWT token
        
        val mockResponse = mapOf(
            "jwt" to "mock-jwt-token-for-development",
            "user" to mapOf(
                "id" to "550e8400-e29b-41d4-a716-446655440010",
                "email" to "demo@example.com",
                "displayName" to "Demo User",
                "photoUrl" to "https://example.com/photo.jpg",
                "bio" to "Demo user for development",
                "hasApartment" to false,
                "openToMatching" to true,
                "createdAt" to "2024-12-01T10:00:00Z"
            )
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
}