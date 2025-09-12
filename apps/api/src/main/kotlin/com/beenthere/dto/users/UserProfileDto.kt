package com.beenthere.dto.users

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * User profile DTOs for displaying user cards and profiles
 */

data class UserProfile(
    @JsonProperty("user")
    val user: UserBasicInfo,
    
    @JsonProperty("ratingsSummary")
    val ratingsSummary: UserRatingsSummary
)

data class UserBasicInfo(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("displayName")
    val displayName: String,
    
    @JsonProperty("photoUrl")
    val photoUrl: String?,
    
    @JsonProperty("bio")
    val bio: String?
)

data class UserRatingsSummary(
    @JsonProperty("roommateAvg")
    val roommateAvg: Double?,
    
    @JsonProperty("count")
    val count: Int
)