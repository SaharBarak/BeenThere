package com.beenthere.api.users

import com.beenthere.common.toResponseEntity
import com.github.michaelbull.result.Ok
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * User profile endpoints for displaying user information in cards.
 * Provides public profile data and ratings summaries.
 */
@RestController
@RequestMapping("/api/v1/users")
class UserProfileController {
    
    /**
     * Get user profile for display in cards.
     * Returns public user info and ratings summary.
     */
    @GetMapping("/{userId}/profile")
    fun getUserProfile(@PathVariable userId: String): ResponseEntity<*> {
        // TODO: Implement user profile lookup
        // TODO: Calculate roommate ratings average and count
        // TODO: Return only public profile fields (no email, etc.)
        
        val mockResponse = mapOf(
            "user" to mapOf(
                "id" to userId,
                "displayName" to "יוסי",
                "photoUrl" to "https://example.com/photos/yossi.jpg",
                "bio" to "סטודנט להנדסה, אוהב בישול ומוזיקה"
            ),
            "ratingsSummary" to mapOf(
                "roommateAvg" to 7.8,
                "count" to 3
            )
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
}