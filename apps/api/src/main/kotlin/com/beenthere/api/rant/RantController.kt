package com.beenthere.api.rant

import com.beenthere.common.toResponseEntity
import com.github.michaelbull.result.Ok
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rant endpoints for rating landlords, apartments, and roommates.
 * Handles both combined rants (landlord + apartment) and separate roommate rants.
 */
@RestController
@RequestMapping("/api/v1/rant")
class RantController {
    
    /**
     * Submit combined landlord + apartment rant.
     * Creates rant_group with associated landlord and apartment ratings.
     */
    @PostMapping
    fun createCombinedRant(@RequestBody request: Map<String, Any>): ResponseEntity<*> {
        // TODO: Implement combined rant creation logic
        // TODO: Hash landlord phone number (HMAC-SHA256)
        // TODO: Create/lookup place from request.place
        // TODO: Create rant_group, ratings_landlord, ratings_apartment in transaction
        
        val mockResponse = mapOf(
            "rantGroupId" to "550e8400-e29b-41d4-a716-446655440002"
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
    
    /**
     * Submit roommate rating.
     * Creates standalone roommate rating entry.
     */
    @PostMapping("/roommate")
    fun createRoommateRant(@RequestBody request: Map<String, Any>): ResponseEntity<*> {
        // TODO: Implement roommate rating creation
        // TODO: Handle both rateeUserId (for existing users) and rateeHint (for unknown users)
        // TODO: Validate scores are in 1-10 range
        
        val mockResponse = mapOf(
            "ratingId" to "550e8400-e29b-41d4-a716-446655440003"
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
}