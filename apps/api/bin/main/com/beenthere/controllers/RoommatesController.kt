package com.beenthere.controllers

import com.beenthere.dto.roommates.*
import com.beenthere.services.RoommatesService
import com.beenthere.common.toResponseEntity
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1")
class RoommatesController(
    private val roommatesService: RoommatesService
) {
    
    @GetMapping("/roommates/feed")
    suspend fun getRoommatesFeed(
        @RequestHeader("X-User-ID") userId: String,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "50") limit: Int,
        @RequestParam filters: Map<String, String> = emptyMap()
    ): ResponseEntity<*> {
        val result = roommatesService.getRoommatesFeed(
            userId = UUID.fromString(userId),
            cursor = cursor,
            limit = limit,
            filters = filters
        )
        return result.toResponseEntity()
    }
    
    @PostMapping("/swipes")
    suspend fun swipeOnRoommate(
        @Valid @RequestBody req: SwipeReq,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<*> {
        val result = roommatesService.swipeOnRoommate(req, UUID.fromString(userId))
        return result.toResponseEntity(HttpStatus.CREATED)
    }
    
    @GetMapping("/matches")
    suspend fun getMatches(
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<*> {
        val result = roommatesService.getMatches(UUID.fromString(userId))
        return result.toResponseEntity()
    }
    
    @GetMapping("/matches/{matchId}/messages")
    suspend fun getMessages(
        @PathVariable matchId: String,
        @RequestHeader("X-User-ID") userId: String,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "50") limit: Int
    ): ResponseEntity<*> {
        val result = roommatesService.getMessages(
            matchId = UUID.fromString(matchId),
            userId = UUID.fromString(userId),
            cursor = cursor,
            limit = limit
        )
        return result.toResponseEntity()
    }
    
    @PostMapping("/matches/{matchId}/messages")
    suspend fun sendMessage(
        @PathVariable matchId: String,
        @RequestHeader("X-User-ID") userId: String,
        @Valid @RequestBody req: SendMessageReq
    ): ResponseEntity<*> {
        val result = roommatesService.sendMessage(
            matchId = UUID.fromString(matchId),
            userId = UUID.fromString(userId),
            req = req
        )
        return result.toResponseEntity(HttpStatus.CREATED)
    }
}