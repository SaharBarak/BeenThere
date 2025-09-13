package com.beenthere.dto.common

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.*

/**
 * Common DTOs mirroring contracts exactly from packages/contracts/src/common.ts
 */

data class PlaceRef(
    @JsonProperty("googlePlaceId")
    val googlePlaceId: String?,
    
    @JsonProperty("formattedAddress")
    val formattedAddress: String?,
    
    @JsonProperty("lat")
    val lat: Double?,
    
    @JsonProperty("lng")
    val lng: Double?
) {
    init {
        require(googlePlaceId != null || (lat != null && lng != null)) {
            "Must provide either googlePlaceId or both lat and lng"
        }
    }
}

data class PaginatedResponse<T>(
    @JsonProperty("items")
    val items: List<T>,
    
    @JsonProperty("nextCursor")
    val nextCursor: String?
)

// Validation annotations for scores (1-10 integers)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Min(1)
@Max(10)
annotation class Score