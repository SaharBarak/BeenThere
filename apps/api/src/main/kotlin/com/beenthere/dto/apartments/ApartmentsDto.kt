package com.beenthere.dto.apartments

import com.beenthere.dto.common.PaginatedResponse
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

/**
 * Apartments/Listings DTOs based on CLAUDE.md requirements
 */

data class ListingCard(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("ownerUserId")
    val ownerUserId: String,
    
    @JsonProperty("placeId")
    val placeId: String,
    
    @JsonProperty("title")
    val title: String,
    
    @JsonProperty("price")
    val price: Int,
    
    @JsonProperty("attrs")
    val attrs: Map<String, Any>?,
    
    @JsonProperty("photos")
    val photos: List<String>,
    
    @JsonProperty("createdAt")
    val createdAt: String,
    
    @JsonProperty("autoAccept")
    val autoAccept: Boolean?
)

typealias ApartmentsFeedRes = PaginatedResponse<ListingCard>

data class CreateListingReq(
    @JsonProperty("placeId")
    @field:NotBlank(message = "Place ID is required")
    val placeId: String,
    
    @JsonProperty("title")
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 100, message = "Title too long")
    val title: String,
    
    @JsonProperty("price")
    @field:Min(value = 0, message = "Price must be non-negative")
    val price: Int,
    
    @JsonProperty("attrs")
    val attrs: Map<String, Any>?,
    
    @JsonProperty("photos")
    val photos: List<String>? = emptyList(),
    
    @JsonProperty("autoAccept")
    val autoAccept: Boolean? = false
)

data class CreateListingRes(
    @JsonProperty("id")
    val id: String
)

enum class ListingSwipeAction {
    LIKE, PASS
}

data class ListingSwipeReq(
    @JsonProperty("listingId")
    @field:NotBlank(message = "Listing ID is required")
    val listingId: String,
    
    @JsonProperty("action")
    val action: ListingSwipeAction
)

data class ListingSwipeRes(
    @JsonProperty("matchId")
    val matchId: String?
)