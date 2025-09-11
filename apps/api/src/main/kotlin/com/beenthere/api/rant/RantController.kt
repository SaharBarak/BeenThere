package com.beenthere.api.rant

import com.beenthere.common.toResponseEntity
import com.beenthere.services.*
import com.github.michaelbull.result.map
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*

/**
 * Rant endpoints for rating landlords, apartments, and roommates.
 * Handles both combined rants (landlord + apartment) and separate roommate rants.
 */
@RestController
@RequestMapping("/api/v1/rant")
class RantController(
    private val combinedRantService: CombinedRantService,
    private val roommateRantService: RoommateRantService
) {
    
    /**
     * Submit combined landlord + apartment rant.
     * Creates rant_group with associated landlord and apartment ratings.
     */
    @PostMapping
    suspend fun createCombinedRant(@RequestBody request: CreateCombinedRantRequestDto): ResponseEntity<*> {
        val serviceRequest = CreateCombinedRantRequest(
            raterUserId = request.raterUserId,
            landlordPhone = request.landlordPhone,
            periodStart = request.periodStart,
            periodEnd = request.periodEnd,
            isCurrentResidence = request.isCurrentResidence ?: false,
            landlordScores = LandlordScores(
                fairness = request.landlordScores.fairness,
                response = request.landlordScores.response,
                maintenance = request.landlordScores.maintenance,
                privacy = request.landlordScores.privacy
            ),
            apartmentScores = ApartmentScores(
                condition = request.apartmentScores.condition,
                noise = request.apartmentScores.noise,
                utilities = request.apartmentScores.utilities,
                sunlightMold = request.apartmentScores.sunlightMold
            ),
            extras = request.extras,
            comment = request.comment,
            place = PlaceRef(
                googlePlaceId = request.place.googlePlaceId,
                formattedAddress = request.place.formattedAddress,
                lat = request.place.lat,
                lng = request.place.lng
            )
        )
        
        val result = combinedRantService.createCombinedRant(serviceRequest)
        
        return result.map { rantGroupId ->
            mapOf("rantGroupId" to rantGroupId.toString())
        }.toResponseEntity()
    }
    
    /**
     * Submit roommate rating.
     * Creates standalone roommate rating entry.
     */
    @PostMapping("/roommate")
    suspend fun createRoommateRant(@RequestBody request: CreateRoommateRantRequestDto): ResponseEntity<*> {
        val serviceRequest = CreateRoommateRantRequest(
            raterUserId = request.raterUserId,
            rateeUserId = request.rateeUserId,
            rateeHint = request.rateeHint?.let { hint ->
                RateeHint(name = hint.name, org = hint.org)
            },
            scores = RoommateScores(
                cleanliness = request.scores.cleanliness,
                communication = request.scores.communication,
                reliability = request.scores.reliability,
                respect = request.scores.respect,
                costSharing = request.scores.costSharing
            ),
            comment = request.comment
        )
        
        val result = roommateRantService.createRoommateRant(serviceRequest)
        
        return result.map { ratingId ->
            mapOf("ratingId" to ratingId.toString())
        }.toResponseEntity()
    }
}

// DTOs for request mapping
data class CreateCombinedRantRequestDto(
    val raterUserId: UUID,
    val landlordPhone: String,
    val periodStart: LocalDate? = null,
    val periodEnd: LocalDate? = null,
    val isCurrentResidence: Boolean? = null,
    val landlordScores: LandlordScoresDto,
    val apartmentScores: ApartmentScoresDto,
    val extras: Map<String, Int>? = null,
    val comment: String? = null,
    val place: PlaceRefDto
)

data class LandlordScoresDto(
    val fairness: Int,
    val response: Int,
    val maintenance: Int,
    val privacy: Int
)

data class ApartmentScoresDto(
    val condition: Int,
    val noise: Int,
    val utilities: Int,
    val sunlightMold: Int
)

data class PlaceRefDto(
    val googlePlaceId: String? = null,
    val formattedAddress: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

data class CreateRoommateRantRequestDto(
    val raterUserId: UUID,
    val rateeUserId: UUID? = null,
    val rateeHint: RateeHintDto? = null,
    val scores: RoommateScoresDto,
    val comment: String? = null
)

data class RateeHintDto(
    val name: String,
    val org: String? = null
)

data class RoommateScoresDto(
    val cleanliness: Int,
    val communication: Int,
    val reliability: Int,
    val respect: Int,
    val costSharing: Int
)