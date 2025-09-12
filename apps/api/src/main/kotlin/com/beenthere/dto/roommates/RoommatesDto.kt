package com.beenthere.dto.roommates

import com.beenthere.dto.common.PaginatedResponse
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

/**
 * Roommates DTOs mirroring contracts exactly from packages/contracts/src/roommates.ts
 */

data class RoommateFeedItem(
    @JsonProperty("userId")
    val userId: String,
    
    @JsonProperty("displayName")
    val displayName: String,
    
    @JsonProperty("photoUrl")
    val photoUrl: String?,
    
    @JsonProperty("bio")
    val bio: String?,
    
    @JsonProperty("prefs")
    val prefs: Map<String, Any>?,
    
    @JsonProperty("hasApartment")
    val hasApartment: Boolean
)

typealias RoommatesFeedRes = PaginatedResponse<RoommateFeedItem>

enum class SwipeAction {
    LIKE, PASS
}

data class SwipeReq(
    @JsonProperty("targetUserId")
    val targetUserId: String,
    
    @JsonProperty("action")
    val action: SwipeAction
)

data class SwipeRes(
    @JsonProperty("matchId")
    val matchId: String?
)

data class Match(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("otherUserId")
    val otherUserId: String,
    
    @JsonProperty("otherUserName")
    val otherUserName: String,
    
    @JsonProperty("otherUserPhotoUrl")
    val otherUserPhotoUrl: String?,
    
    @JsonProperty("createdAt")
    val createdAt: String,
    
    @JsonProperty("lastMessageAt")
    val lastMessageAt: String?,
    
    @JsonProperty("lastMessage")
    val lastMessage: String?
)

data class MatchesRes(
    @JsonProperty("matches")
    val matches: List<Match>
)

data class Message(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("senderUserId")
    val senderUserId: String,
    
    @field:NotBlank(message = "Message body cannot be empty")
    @JsonProperty("body")
    val body: String,
    
    @JsonProperty("createdAt")
    val createdAt: String
)

typealias MessagesRes = PaginatedResponse<Message>

data class SendMessageReq(
    @field:NotBlank(message = "Message body cannot be empty")
    @field:Size(max = 1000, message = "Message too long")
    @JsonProperty("body")
    val body: String
)

data class SendMessageRes(
    @JsonProperty("id")
    val id: String
)