package com.beenthere.dto.places

import com.beenthere.dto.common.PlaceRef
import com.beenthere.dto.common.Score
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.*

/**
 * Places DTOs mirroring contracts exactly from packages/contracts/src/places.ts
 */

typealias PlaceSnapReq = PlaceRef

data class PlaceSnapRes(
    @JsonProperty("placeId")
    val placeId: String
)

data class Place(
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

data class LandlordScores(
    @field:Score
    @JsonProperty("fairness")
    val fairness: Int,
    
    @field:Score
    @JsonProperty("response")
    val response: Int,
    
    @field:Score
    @JsonProperty("maintenance")
    val maintenance: Int,
    
    @field:Score
    @JsonProperty("privacy")
    val privacy: Int
)

data class ApartmentScores(
    @field:Score
    @JsonProperty("condition")
    val condition: Int,
    
    @field:Score
    @JsonProperty("noise")
    val noise: Int,
    
    @field:Score
    @JsonProperty("utilities")
    val utilities: Int,
    
    @field:Score
    @JsonProperty("sunlightMold")
    val sunlightMold: Int
)

data class Extras(
    @field:Score
    @JsonProperty("neighborsNoise")
    val neighborsNoise: Int?,
    
    @field:Score
    @JsonProperty("roofCommon")
    val roofCommon: Int?,
    
    @field:Score
    @JsonProperty("elevatorSolar")
    val elevatorSolar: Int?,
    
    @field:Score
    @JsonProperty("neighSafety")
    val neighSafety: Int?,
    
    @field:Score
    @JsonProperty("neighServices")
    val neighServices: Int?,
    
    @field:Score
    @JsonProperty("neighTransit")
    val neighTransit: Int?,
    
    @field:Score
    @JsonProperty("priceFairness")
    val priceFairness: Int?
)

data class RecentRating(
    @JsonProperty("at")
    val at: String,
    
    @JsonProperty("landlordScores")
    val landlordScores: LandlordScores?,
    
    @JsonProperty("apartmentScores")
    val apartmentScores: ApartmentScores?,
    
    @JsonProperty("extras")
    val extras: Extras?,
    
    @JsonProperty("comment")
    val comment: String?
)

data class PlaceProfileRes(
    @JsonProperty("place")
    val place: Place,
    
    @JsonProperty("ratings")
    val ratings: PlaceRatings
)

data class PlaceRatings(
    @JsonProperty("counts")
    val counts: RatingCounts,
    
    @JsonProperty("averages")
    val averages: RatingAverages,
    
    @JsonProperty("recent")
    val recent: List<RecentRating>
)

data class RatingCounts(
    @field:PositiveOrZero
    @JsonProperty("landlord")
    val landlord: Int,
    
    @field:PositiveOrZero
    @JsonProperty("apartment")
    val apartment: Int
)

data class RatingAverages(
    @JsonProperty("landlord")
    val landlord: Double?,
    
    @JsonProperty("apartment")
    val apartment: Double?,
    
    @JsonProperty("extras")
    val extras: Map<String, Double>?
)