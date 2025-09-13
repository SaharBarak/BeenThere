package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.dto.rant.CreateRantCombinedReq
import com.beenthere.dto.rant.CreateRantCombinedRes
import com.beenthere.dto.rant.CreateRoommateRantReq
import com.beenthere.dto.rant.CreateRoommateRantRes
import com.beenthere.entities.*
import com.beenthere.repositories.*
import com.beenthere.util.PhoneUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class RantService(
    private val placeService: PlaceService,
    private val phoneUtils: PhoneUtils,
    private val landlordRepository: LandlordRepository,
    private val rantGroupRepository: RantGroupRepository,
    private val ratingLandlordRepository: RatingLandlordRepository,
    private val ratingApartmentRepository: RatingApartmentRepository,
    private val ratingRoommateRepository: RatingRoommateRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper
) {
    
    @Transactional
    suspend fun createCombinedRant(req: CreateRantCombinedReq, raterUserId: UUID): Result<CreateRantCombinedRes> {
        return try {
            // 1. Process phone number (normalize + hash)
            val phoneHashResult = phoneUtils.processPhoneForStorage(req.landlordPhone)
            if (phoneHashResult.isFailure) {
                return Result.failure(phoneHashResult.exceptionOrNull() ?: ServiceError.PhoneValidationError(req.landlordPhone))
            }
            val phoneHash = phoneHashResult.getOrThrow()
            
            // 2. Find or create landlord
            val landlord = landlordRepository.findByPhoneHash(phoneHash)
                ?: landlordRepository.save(LandlordEntity(phoneHash = phoneHash))
            
            // 3. Snap place to get place ID
            val placeSnapResult = placeService.snapPlace(req.place)
            if (placeSnapResult.isFailure) {
                return Result.failure(placeSnapResult.exceptionOrNull() ?: ServiceError.DatabaseError(RuntimeException("Failed to snap place")))
            }
            val placeId = UUID.fromString(placeSnapResult.getOrThrow().placeId)
            
            // 4. Create rant group
            val rantGroup = RantGroupEntity(
                raterUserId = raterUserId,
                landlordId = landlord.id!!,
                placeId = placeId,
                periodStart = req.periodStart?.let { LocalDate.parse(it) },
                periodEnd = req.periodEnd?.let { LocalDate.parse(it) },
                isCurrentResidence = req.isCurrentResidence ?: false,
                comment = req.comment
            )
            val savedRantGroup = rantGroupRepository.save(rantGroup)
            
            // 5. Create landlord rating
            val landlordRating = RatingLandlordEntity(
                rantGroupId = savedRantGroup.id!!,
                scores = objectMapper.valueToTree(req.landlordScores)
            )
            ratingLandlordRepository.save(landlordRating)
            
            // 6. Create apartment rating
            val apartmentRating = RatingApartmentEntity(
                rantGroupId = savedRantGroup.id!!,
                scores = objectMapper.valueToTree(req.apartmentScores),
                extras = req.extras?.let { objectMapper.valueToTree(it) }
            )
            ratingApartmentRepository.save(apartmentRating)
            
            Result.success(CreateRantCombinedRes(rantGroupId = savedRantGroup.id.toString()))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    @Transactional
    suspend fun createRoommateRant(req: CreateRoommateRantReq, raterUserId: UUID): Result<CreateRoommateRantRes> {
        return try {
            // Validate that ratee user exists if rateeUserId is provided
            var rateeUserId: UUID? = null
            if (req.rateeUserId != null) {
                val rateeUuid = UUID.fromString(req.rateeUserId)
                val rateeExists = userRepository.existsById(rateeUuid)
                if (!rateeExists) {
                    return Result.failure(ServiceError.UserNotFound(req.rateeUserId))
                }
                rateeUserId = rateeUuid
            }
            
            // Create roommate rating
            val roommateRating = RatingRoommateEntity(
                raterUserId = raterUserId,
                rateeUserId = rateeUserId,
                rateeHint = req.rateeHint?.let { objectMapper.valueToTree(it) },
                scores = objectMapper.valueToTree(req.scores),
                comment = req.comment
            )
            val savedRating = ratingRoommateRepository.save(roommateRating)
            
            Result.success(CreateRoommateRantRes(ratingId = savedRating.id.toString()))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
}