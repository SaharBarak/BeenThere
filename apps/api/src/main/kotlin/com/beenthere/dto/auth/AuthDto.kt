package com.beenthere.dto.auth

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.*

/**
 * Auth DTOs mirroring contracts exactly from packages/contracts/src/auth.ts
 */

data class GoogleAuthReq(
    @field:NotBlank(message = "ID token is required")
    @JsonProperty("idToken")
    val idToken: String
)

data class UserInfo(
    @JsonProperty("id")
    val id: String,
    
    @field:Email
    @JsonProperty("email")
    val email: String,
    
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    @JsonProperty("displayName")
    val displayName: String,
    
    @JsonProperty("photoUrl")
    val photoUrl: String?,
    
    @field:Size(max = 300)
    @JsonProperty("bio")
    val bio: String?,
    
    @JsonProperty("hasApartment")
    val hasApartment: Boolean = false,
    
    @JsonProperty("openToMatching")
    val openToMatching: Boolean = true,
    
    @JsonProperty("createdAt")
    val createdAt: String
)

data class GoogleAuthRes(
    @field:NotBlank(message = "JWT token is required")
    @JsonProperty("jwt")
    val jwt: String,
    
    @field:Valid
    @JsonProperty("user")
    val user: UserInfo
)

data class UserProfile(
    @JsonProperty("user")
    val user: UserProfileData,
    
    @JsonProperty("ratingsSummary")
    val ratingsSummary: RatingsSummary
)

data class UserProfileData(
    @JsonProperty("id")
    val id: String,
    
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    @JsonProperty("displayName")
    val displayName: String,
    
    @JsonProperty("photoUrl")
    val photoUrl: String?,
    
    @field:Size(max = 300)
    @JsonProperty("bio")
    val bio: String?
)

data class RatingsSummary(
    @field:DecimalMin("1.0")
    @field:DecimalMax("10.0")
    @JsonProperty("roommateAvg")
    val roommateAvg: Double?,
    
    @field:PositiveOrZero
    @JsonProperty("count")
    val count: Int
)

data class UpdateProfileReq(
    @field:Size(min = 1, max = 100, message = "Display name must be 1-100 characters")
    @JsonProperty("displayName")
    val displayName: String?,
    
    @field:Size(max = 300, message = "Bio too long")
    @JsonProperty("bio")
    val bio: String?,
    
    @JsonProperty("hasApartment")
    val hasApartment: Boolean?,
    
    @JsonProperty("openToMatching")
    val openToMatching: Boolean?
)

data class UpdateProfileRes(
    @JsonProperty("success")
    val success: Boolean
)