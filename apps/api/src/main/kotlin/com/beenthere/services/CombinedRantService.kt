package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.entities.*
import com.beenthere.repositories.*
import com.beenthere.util.PhoneUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

data class LandlordScores(
    val fairness: Int,
    val response: Int,
    val maintenance: Int,
    val privacy: Int
)

data class ApartmentScores(
    val condition: Int,
    val noise: Int,
    val utilities: Int,
    val sunlightMold: Int
)

data class CreateCombinedRantRequest(
    val raterUserId: UUID,
    val landlordPhone: String,
    val periodStart: LocalDate? = null,
    val periodEnd: LocalDate? = null,
    val isCurrentResidence: Boolean = false,
    val landlordScores: LandlordScores,
    val apartmentScores: ApartmentScores,
    val extras: Map<String, Int>? = null,
    val comment: String? = null,
    val place: PlaceRef
)

@Service
class CombinedRantService(
    private val phoneUtils: PhoneUtils,
    private val placeService: PlaceService,
    private val landlordRepository: LandlordRepository,
    private val rantGroupRepository: RantGroupRepository,
    private val ratingLandlordRepository: RatingLandlordRepository,
    private val ratingApartmentRepository: RatingApartmentRepository,
    private val objectMapper: ObjectMapper
) {
    
    /**
     * Create a combined rant (landlord + apartment) with transactional integrity.
     * This is the core business logic for rant submission.
     */
    @Transactional
    suspend fun createCombinedRant(request: CreateCombinedRantRequest): Result<UUID, ServiceError> {
        return try {
            // Validate scores are in range 1-10
            validateScores(request.landlordScores, request.apartmentScores, request.extras)
                .mapError { return Err(it) }
            
            // Validate comment length
            if (request.comment != null && request.comment.length > 300) {
                return Err(ServiceError.ValidationError("comment", "Comment cannot exceed 300 characters"))
            }
            
            // Hash the landlord phone number
            val phoneHash = phoneUtils.normalizeAndHash(request.landlordPhone)
                .mapError { return Err(it) }
            
            // Get or create landlord
            val landlord = getOrCreateLandlord(phoneHash)
                .mapError { return Err(it) }
            
            // Get or create place
            val place = placeService.getOrCreatePlace(request.place)
                .mapError { return Err(it) }
            
            // Create rant group
            val rantGroup = RantGroupEntity(
                raterUserId = request.raterUserId,
                landlordId = landlord.id,
                placeId = place.id,
                periodStart = request.periodStart,
                periodEnd = request.periodEnd,
                isCurrentResidence = request.isCurrentResidence,
                comment = request.comment
            )
            val savedRantGroup = rantGroupRepository.save(rantGroup)
            
            // Create landlord rating
            val landlordScoresJson = objectMapper.writeValueAsString(request.landlordScores)
            val landlordRating = RatingLandlordEntity(
                rantGroupId = savedRantGroup.id,
                scores = landlordScoresJson
            )
            ratingLandlordRepository.save(landlordRating)
            
            // Create apartment rating
            val apartmentScoresJson = objectMapper.writeValueAsString(request.apartmentScores)
            val extrasJson = request.extras?.let { objectMapper.writeValueAsString(it) }
            val apartmentRating = RatingApartmentEntity(
                rantGroupId = savedRantGroup.id,
                scores = apartmentScoresJson,
                extras = extrasJson
            )
            ratingApartmentRepository.save(apartmentRating)
            
            Ok(savedRantGroup.id)
            
        } catch (e: Exception) {
            Err(ServiceError.DatabaseError("Failed to create combined rant", e))
        }
    }
    
    private suspend fun getOrCreateLandlord(phoneHash: String): Result<LandlordEntity, ServiceError> {
        return try {
            // Check if landlord already exists
            val existingLandlord = landlordRepository.findByPhoneHash(phoneHash)
            if (existingLandlord != null) {
                Ok(existingLandlord)
            } else {
                // Create new landlord
                val newLandlord = LandlordEntity(phoneHash = phoneHash)
                val savedLandlord = landlordRepository.save(newLandlord)
                Ok(savedLandlord)
            }
        } catch (e: Exception) {
            Err(ServiceError.DatabaseError("Failed to get or create landlord", e))
        }
    }
    
    private fun validateScores(
        landlordScores: LandlordScores,
        apartmentScores: ApartmentScores,
        extras: Map<String, Int>?
    ): Result<Unit, ServiceError.ValidationError> {
        
        val allScores = listOf(
            "fairness" to landlordScores.fairness,
            "response" to landlordScores.response,
            "maintenance" to landlordScores.maintenance,
            "privacy" to landlordScores.privacy,
            "condition" to apartmentScores.condition,
            "noise" to apartmentScores.noise,
            "utilities" to apartmentScores.utilities,
            "sunlightMold" to apartmentScores.sunlightMold
        ) + (extras?.toList() ?: emptyList())
        
        for ((field, score) in allScores) {
            if (score < 1 || score > 10) {
                return Err(ServiceError.ValidationError(field, "Score must be between 1 and 10"))
            }
        }
        
        return Ok(Unit)
    }
}