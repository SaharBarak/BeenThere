package com.beenthere.dto.rant

import com.beenthere.dto.common.PlaceRef
import com.beenthere.dto.common.Score
import com.beenthere.dto.places.LandlordScores
import com.beenthere.dto.places.ApartmentScores
import com.beenthere.dto.places.Extras
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.*

/**
 * Rant DTOs mirroring contracts exactly from packages/contracts/src/rant.ts
 */

data class CreateRantCombinedReq(
    @field:NotBlank(message = "Landlord phone is required")
    @JsonProperty("landlordPhone")
    val landlordPhone: String,
    
    @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in YYYY-MM-DD format")
    @JsonProperty("periodStart")
    val periodStart: String?,
    
    @field:Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in YYYY-MM-DD format")
    @JsonProperty("periodEnd")
    val periodEnd: String?,
    
    @JsonProperty("isCurrentResidence")
    val isCurrentResidence: Boolean?,
    
    @field:Valid
    @JsonProperty("landlordScores")
    val landlordScores: LandlordScores,
    
    @field:Valid
    @JsonProperty("apartmentScores")
    val apartmentScores: ApartmentScores,
    
    @field:Valid
    @JsonProperty("extras")
    val extras: Extras?,
    
    @field:Size(max = 300)
    @JsonProperty("comment")
    val comment: String?,
    
    @field:Valid
    @JsonProperty("place")
    val place: PlaceRef
)

data class CreateRantCombinedRes(
    @JsonProperty("rantGroupId")
    val rantGroupId: String
)

data class RoommateScores(
    @field:Score
    @JsonProperty("cleanliness")
    val cleanliness: Int,
    
    @field:Score
    @JsonProperty("communication")
    val communication: Int,
    
    @field:Score
    @JsonProperty("reliability")
    val reliability: Int,
    
    @field:Score
    @JsonProperty("respect")
    val respect: Int,
    
    @field:Score
    @JsonProperty("costSharing")
    val costSharing: Int
)

data class RateeHint(
    @field:NotBlank(message = "Name is required")
    @JsonProperty("name")
    val name: String,
    
    @JsonProperty("org")
    val org: String?
)

data class CreateRoommateRantReq(
    @JsonProperty("rateeUserId")
    val rateeUserId: String?,
    
    @field:Valid
    @JsonProperty("rateeHint")
    val rateeHint: RateeHint?,
    
    @field:Valid
    @JsonProperty("scores")
    val scores: RoommateScores,
    
    @JsonProperty("comment")
    val comment: String?
) {
    init {
        require(rateeUserId != null || rateeHint != null) {
            "Must provide either rateeUserId or rateeHint"
        }
    }
}

data class CreateRoommateRantRes(
    @JsonProperty("ratingId")
    val ratingId: String
)