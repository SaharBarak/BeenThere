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
    val autoAccept: Boolean?,
    
    // Roommate Group Fields
    @JsonProperty("type")
    val type: String,
    
    @JsonProperty("capacityTotal")
    val capacityTotal: Int?,
    
    @JsonProperty("spotsAvailable")
    val spotsAvailable: Int?,
    
    @JsonProperty("moveInDate")
    val moveInDate: String?,
    
    @JsonProperty("rentPerRoom")
    val rentPerRoom: Int?,
    
    // Enhanced Features
    @JsonProperty("roommates")
    val roommates: List<RoommateCard>?,
    
    @JsonProperty("stats")
    val stats: ListingStats
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
    val autoAccept: Boolean? = false,
    
    // Roommate Group Fields
    @JsonProperty("type")
    val type: String = "ENTIRE_PLACE", // "ENTIRE_PLACE" | "ROOMMATE_GROUP"
    
    @JsonProperty("capacityTotal")
    val capacityTotal: Int? = null,
    
    @JsonProperty("spotsAvailable")
    val spotsAvailable: Int? = null,
    
    @JsonProperty("moveInDate")
    val moveInDate: String? = null, // "2025-10-01"
    
    @JsonProperty("rentPerRoom")
    val rentPerRoom: Int? = null,
    
    @JsonProperty("description")
    val description: String? = null
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

// ===== ROOMMATE GROUP DTOs =====

data class RoommateCard(
    @JsonProperty("userId")
    val userId: String,
    
    @JsonProperty("displayName")
    val displayName: String,
    
    @JsonProperty("photoUrl")
    val photoUrl: String?,
    
    @JsonProperty("role")
    val role: String, // "OWNER" | "TENANT"
    
    @JsonProperty("isAdmin")
    val isAdmin: Boolean
)

data class ListingStats(
    @JsonProperty("landlordAvg")
    val landlordAvg: Double?,
    
    @JsonProperty("landlordCount")
    val landlordCount: Int,
    
    @JsonProperty("apartmentAvg")
    val apartmentAvg: Double?,
    
    @JsonProperty("apartmentCount")
    val apartmentCount: Int,
    
    @JsonProperty("neighborsAvg")
    val neighborsAvg: Double?,
    
    @JsonProperty("neighborsCount")
    val neighborsCount: Int,
    
    @JsonProperty("roommatesAvg")
    val roommatesAvg: Double?,
    
    @JsonProperty("roommatesCount")
    val roommatesCount: Int
)

// ===== PROFILE DTOs =====

data class UserDetails(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("displayName")
    val displayName: String,
    
    @JsonProperty("photoUrl")
    val photoUrl: String?,
    
    @JsonProperty("bio")
    val bio: String?
)

data class RatingsSummary(
    @JsonProperty("avg")
    val avg: Double?,
    
    @JsonProperty("count")
    val count: Int
)

data class RoommateProfile(
    @JsonProperty("user")
    val user: UserDetails,
    
    @JsonProperty("role")
    val role: String,
    
    @JsonProperty("joinedAt")
    val joinedAt: String,
    
    @JsonProperty("roommateRating")
    val roommateRating: RatingsSummary
)

data class PlaceDetails(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("googlePlaceId")
    val googlePlaceId: String?,
    
    @JsonProperty("formattedAddress")
    val formattedAddress: String?,
    
    @JsonProperty("lat")
    val lat: Double?,
    
    @JsonProperty("lng")
    val lng: Double?
)

data class ListingDetails(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("ownerUserId")
    val ownerUserId: String,
    
    @JsonProperty("title")
    val title: String,
    
    @JsonProperty("description")
    val description: String?,
    
    @JsonProperty("price")
    val price: Int,
    
    @JsonProperty("type")
    val type: String,
    
    @JsonProperty("capacityTotal")
    val capacityTotal: Int?,
    
    @JsonProperty("spotsAvailable")
    val spotsAvailable: Int?,
    
    @JsonProperty("moveInDate")
    val moveInDate: String?,
    
    @JsonProperty("rentPerRoom")
    val rentPerRoom: Int?,
    
    @JsonProperty("photos")
    val photos: List<String>,
    
    @JsonProperty("attrs")
    val attrs: Map<String, Any>?,
    
    @JsonProperty("autoAccept")
    val autoAccept: Boolean,
    
    @JsonProperty("createdAt")
    val createdAt: String
)

data class ApartmentRatings(
    @JsonProperty("landlord")
    val landlord: RatingBreakdown?,
    
    @JsonProperty("apartment")
    val apartment: RatingBreakdown?,
    
    @JsonProperty("neighbors")
    val neighbors: RatingBreakdown?,
    
    @JsonProperty("recent")
    val recent: List<RecentRating>
)

data class RatingBreakdown(
    @JsonProperty("avg")
    val avg: Double?,
    
    @JsonProperty("count")
    val count: Int,
    
    @JsonProperty("scores")
    val scores: Map<String, Double>
)

data class RecentRating(
    @JsonProperty("at")
    val at: String,
    
    @JsonProperty("landlordScores")
    val landlordScores: Map<String, Int>?,
    
    @JsonProperty("apartmentScores")
    val apartmentScores: Map<String, Int>?,
    
    @JsonProperty("neighborScores")
    val neighborScores: Map<String, Int>?,
    
    @JsonProperty("comment")
    val comment: String?
)

// ===== APARTMENT PROFILE RESPONSES =====

data class SeekerProfileRes(
    @JsonProperty("user")
    val user: UserDetails,
    
    @JsonProperty("photos")
    val photos: List<String>,
    
    @JsonProperty("preferences")
    val preferences: Map<String, Any>?,
    
    @JsonProperty("roommateRating")
    val roommateRating: RatingsSummary
)

data class ApartmentProfileRes(
    @JsonProperty("listing")
    val listing: ListingDetails,
    
    @JsonProperty("place")
    val place: PlaceDetails,
    
    @JsonProperty("ratings")
    val ratings: ApartmentRatings,
    
    // For ROOMMATE_GROUP only
    @JsonProperty("roommates")
    val roommates: List<RoommateProfile>?,
    
    @JsonProperty("groupPrefs")
    val groupPrefs: Map<String, Any>?
)

// ===== MEMBER MANAGEMENT DTOs =====

data class AddMemberReq(
    @JsonProperty("userId")
    @field:NotBlank(message = "User ID is required")
    val userId: String,
    
    @JsonProperty("role")
    val role: String = "TENANT", // "OWNER" | "TENANT"
    
    @JsonProperty("displayOrder")
    val displayOrder: Int = 0
)

data class CandidatesRes(
    @JsonProperty("candidates")
    val candidates: List<CandidateCard>,
    
    @JsonProperty("nextCursor")
    val nextCursor: String?
)

data class CandidateCard(
    @JsonProperty("user")
    val user: UserDetails,
    
    @JsonProperty("swipedAt")
    val swipedAt: String,
    
    @JsonProperty("roommateRating")
    val roommateRating: RatingsSummary,
    
    @JsonProperty("compatibilityScore")
    val compatibilityScore: Double?
)

data class VoteReq(
    @JsonProperty("action")
    val action: String // "LIKE" | "PASS"
)