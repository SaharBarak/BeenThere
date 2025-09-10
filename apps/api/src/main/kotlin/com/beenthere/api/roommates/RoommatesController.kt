package com.beenthere.api.roommates

import com.beenthere.common.toResponseEntity
import com.github.michaelbull.result.Ok
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Roommate endpoints for swiping, matching, and messaging.
 * Handles the roommate discovery and communication workflow.
 */
@RestController
@RequestMapping("/api/v1")
class RoommatesController {
    
    /**
     * Get roommate feed with cursor-based pagination.
     * Returns potential roommates based on filters and preferences.
     */
    @GetMapping("/roommates/feed")
    fun getRoommateFeed(
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(required = false) hasApartment: Boolean?
    ): ResponseEntity<*> {
        // TODO: Implement cursor-based pagination
        // TODO: Apply filters (hasApartment, etc.)
        // TODO: Exclude already swiped users
        // TODO: Return seek cursor for next page
        
        val mockResponse = mapOf(
            "items" to listOf(
                mapOf(
                    "userId" to "550e8400-e29b-41d4-a716-446655440010",
                    "displayName" to "יוסי",
                    "photoUrl" to "https://example.com/photos/yossi.jpg",
                    "bio" to "סטודנט להנדסה, אוהב בישול ומוזיקה",
                    "prefs" to mapOf(
                        "pets" to false,
                        "smoking" to false,
                        "schedule" to "morning_person"
                    ),
                    "hasApartment" to true
                ),
                mapOf(
                    "userId" to "550e8400-e29b-41d4-a716-446655440011",
                    "displayName" to "שרה",
                    "bio" to "מעצבת גרפית, מחפשת מקום שקט ללמוד",
                    "hasApartment" to false
                )
            ),
            "nextCursor" to "eyJ0aW1lc3RhbXAiOjE2NzAyNTc2MDAsImlkIjoiNTUwZTg0MDAtZTI5Yi00MWQ0LWE3MTYtNDQ2NjU1NDQwMDExIn0="
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
    
    /**
     * Swipe on a roommate (LIKE or PASS).
     * Creates match if mutual LIKE exists.
     */
    @PostMapping("/swipes")
    fun swipeOnRoommate(@RequestBody request: Map<String, Any>): ResponseEntity<*> {
        // TODO: Implement swipe logic
        // TODO: Check subscription status for rate limiting
        // TODO: Create/update roommate_swipes entry
        // TODO: Check for mutual LIKE and create match if found
        
        val mockResponse = mapOf(
            "matchId" to "550e8400-e29b-41d4-a716-446655440020"
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
    
    /**
     * Get user's matches with last message info.
     * Returns list of active roommate matches.
     */
    @GetMapping("/matches")
    fun getMatches(): ResponseEntity<*> {
        // TODO: Implement matches retrieval for current user
        // TODO: Include last message timestamp and preview
        // TODO: Order by last activity
        
        val mockResponse = mapOf(
            "matches" to listOf(
                mapOf(
                    "id" to "550e8400-e29b-41d4-a716-446655440020",
                    "otherUserId" to "550e8400-e29b-41d4-a716-446655440010",
                    "otherUserName" to "יוסי",
                    "otherUserPhotoUrl" to "https://example.com/photos/yossi.jpg",
                    "createdAt" to "2024-12-01T15:30:00Z",
                    "lastMessageAt" to "2024-12-01T16:45:00Z",
                    "lastMessage" to "היי! איך דברים?"
                )
            )
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
    
    /**
     * Get messages for a specific match.
     * Returns paginated chat history.
     */
    @GetMapping("/matches/{matchId}/messages")
    fun getMessages(
        @PathVariable matchId: String,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "50") limit: Int
    ): ResponseEntity<*> {
        // TODO: Implement message retrieval with cursor pagination
        // TODO: Verify user has access to this match
        // TODO: Order by creation time (newest first for pagination)
        
        val mockResponse = mapOf(
            "items" to listOf(
                mapOf(
                    "id" to "550e8400-e29b-41d4-a716-446655440030",
                    "senderUserId" to "550e8400-e29b-41d4-a716-446655440010",
                    "body" to "היי! איך דברים?",
                    "createdAt" to "2024-12-01T16:45:00Z"
                ),
                mapOf(
                    "id" to "550e8400-e29b-41d4-a716-446655440031",
                    "senderUserId" to "550e8400-e29b-41d4-a716-446655440011",
                    "body" to "שלום! הכל טוב, תודה. אתה עדיין מחפש שותף?",
                    "createdAt" to "2024-12-01T16:47:00Z"
                )
            ),
            "nextCursor" to "eyJ0aW1lc3RhbXAiOjE3MDE0MzkyMjAsImlkIjoiNTUwZTg0MDAtZTI5Yi00MWQ0LWE3MTYtNDQ2NjU1NDQwMDMxIn0="
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
    
    /**
     * Send message in a match.
     * Creates new message in the chat thread.
     */
    @PostMapping("/matches/{matchId}/messages")
    fun sendMessage(
        @PathVariable matchId: String,
        @RequestBody request: Map<String, Any>
    ): ResponseEntity<*> {
        // TODO: Implement message sending
        // TODO: Verify user has access to this match
        // TODO: Check subscription status for rate limiting
        // TODO: Validate message content and length
        
        val mockResponse = mapOf(
            "id" to "550e8400-e29b-41d4-a716-446655440032"
        )
        
        return Ok(mockResponse).toResponseEntity()
    }
}