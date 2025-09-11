package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.entities.RatingRoommateEntity
import com.beenthere.repositories.RatingRoommateRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import java.util.*

data class RoommateScores(
    val cleanliness: Int,
    val communication: Int,
    val reliability: Int,
    val respect: Int,
    val costSharing: Int
)

data class RateeHint(
    val name: String,
    val org: String? = null
)

data class CreateRoommateRantRequest(
    val raterUserId: UUID,
    val rateeUserId: UUID? = null,
    val rateeHint: RateeHint? = null,
    val scores: RoommateScores,
    val comment: String? = null
)

@Service
class RoommateRantService(
    private val ratingRoommateRepository: RatingRoommateRepository,
    private val objectMapper: ObjectMapper
) {
    
    /**
     * Create a roommate rating/rant.
     * Can rate a specific user (if rateeUserId provided) or anonymous hint-based rating.
     */
    suspend fun createRoommateRant(request: CreateRoommateRantRequest): Result<UUID, ServiceError> {
        return try {
            // Validate that either rateeUserId or rateeHint is provided
            if (request.rateeUserId == null && request.rateeHint == null) {
                return Err(ServiceError.ValidationError("ratee", "Must provide either rateeUserId or rateeHint"))
            }
            
            // Validate scores are in range 1-10
            validateRoommateScores(request.scores)
                .mapError { return Err(it) }
            
            // Validate comment length
            if (request.comment != null && request.comment.length > 300) {
                return Err(ServiceError.ValidationError("comment", "Comment cannot exceed 300 characters"))
            }
            
            // Validate rateeHint name if provided
            if (request.rateeHint != null && request.rateeHint.name.isBlank()) {
                return Err(ServiceError.ValidationError("rateeHint.name", "Name is required in ratee hint"))
            }
            
            // Create roommate rating
            val scoresJson = objectMapper.writeValueAsString(request.scores)
            val rateeHintJson = request.rateeHint?.let { objectMapper.writeValueAsString(it) }
            
            val roommateRating = RatingRoommateEntity(
                raterUserId = request.raterUserId,
                rateeUserId = request.rateeUserId,
                rateeHint = rateeHintJson,
                scores = scoresJson,
                comment = request.comment
            )
            
            val savedRating = ratingRoommateRepository.save(roommateRating)
            Ok(savedRating.id)
            
        } catch (e: Exception) {
            Err(ServiceError.DatabaseError("Failed to create roommate rating", e))
        }
    }
    
    private fun validateRoommateScores(scores: RoommateScores): Result<Unit, ServiceError.ValidationError> {
        val allScores = listOf(
            "cleanliness" to scores.cleanliness,
            "communication" to scores.communication,
            "reliability" to scores.reliability,
            "respect" to scores.respect,
            "costSharing" to scores.costSharing
        )
        
        for ((field, score) in allScores) {
            if (score < 1 || score > 10) {
                return Err(ServiceError.ValidationError(field, "Score must be between 1 and 10"))
            }
        }
        
        return Ok(Unit)
    }
}