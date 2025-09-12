package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.dto.users.*
import com.beenthere.dto.rant.RoommateScores
import com.beenthere.repositories.*
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserProfileService(
    private val userRepository: UserRepository,
    private val ratingRoommateRepository: RatingRoommateRepository,
    private val objectMapper: ObjectMapper
) {
    
    suspend fun getUserProfile(userId: UUID): Result<UserProfile> {
        return try {
            // Get user
            val user = userRepository.findById(userId)
                ?: return Result.failure(ServiceError.UserNotFound(userId.toString()))
            
            // Get roommate ratings for this user
            val roommateRatings = ratingRoommateRepository.findByRateeUserIdOrderByCreatedAtDesc(userId).toList()
            
            // Calculate roommate average
            val roommateAvg = if (roommateRatings.isNotEmpty()) {
                roommateRatings.map { rating ->
                    val scores = objectMapper.convertValue(rating.scores, RoommateScores::class.java)
                    (scores.cleanliness + scores.communication + scores.reliability + 
                     scores.respect + scores.costSharing) / 5.0
                }.average()
            } else null
            
            Result.success(UserProfile(
                user = UserBasicInfo(
                    id = user.id.toString(),
                    displayName = user.displayName,
                    photoUrl = user.photoUrl,
                    bio = user.bio
                ),
                ratingsSummary = UserRatingsSummary(
                    roommateAvg = roommateAvg,
                    count = roommateRatings.size
                )
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
}