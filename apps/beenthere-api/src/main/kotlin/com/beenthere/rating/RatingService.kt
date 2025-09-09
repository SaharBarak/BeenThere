package com.beenthere.rating

import com.beenthere.error.ServiceError
import com.michaelbull.result.Err
import com.michaelbull.result.Ok
import com.michaelbull.result.Result
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class RatingService(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
) {
    fun rateHouse(request: RateHouseRequest): Mono<Result<HouseRatingEntity, ServiceError>> {
        val userId = "current-user-id" // This would come from security context

        val rating =
            HouseRatingEntity(
                id = UUID.randomUUID().toString(),
                raterId = userId,
                listingId = request.listingId ?: "",
                rating = request.scores.overall,
                comment = request.comment,
                cleanlinessRating = request.scores.cleanliness,
                amenitiesRating = request.scores.amenities,
                locationRating = request.scores.location,
                valueRating = request.scores.valueForMoney,
            )

        return r2dbcEntityTemplate.insert(rating)
            .map { Ok(it) }
            .onErrorReturn(Err(ServiceError.InternalServerError("Failed to create house rating")))
    }

    fun rateRoommate(request: RateRoommateRequest): Mono<Result<RoommateRatingEntity, ServiceError>> {
        val userId = "current-user-id" // This would come from security context

        val rating =
            RoommateRatingEntity(
                id = UUID.randomUUID().toString(),
                raterId = userId,
                ratedUserId = request.rateeUserId,
                rating = request.scores.overall,
                comment = request.comment,
                communicationRating = request.scores.communication,
                cleanlinessRating = request.scores.tidiness,
                respectRating = request.scores.respectfulness,
                socialRating = request.scores.socialCompatibility,
            )

        return r2dbcEntityTemplate.insert(rating)
            .map { Ok(it) }
            .onErrorReturn(Err(ServiceError.InternalServerError("Failed to create roommate rating")))
    }

    fun rateLandlord(request: RateLandlordRequest): Mono<Result<LandlordRatingEntity, ServiceError>> {
        val userId = "current-user-id" // This would come from security context

        val rating =
            LandlordRatingEntity(
                id = UUID.randomUUID().toString(),
                raterId = userId,
                landlordId = request.landlordUserId,
                rating = request.scores.overall,
                comment = request.comment,
                responsivenessRating = request.scores.responsiveness,
                fairnessRating = request.scores.fairness,
                maintenanceRating = request.scores.maintenanceQuality,
            )

        return r2dbcEntityTemplate.insert(rating)
            .map { Ok(it) }
            .onErrorReturn(Err(ServiceError.InternalServerError("Failed to create landlord rating")))
    }
}

data class RateHouseRequest(
    val listingId: String? = null,
    val addressHash: String? = null,
    val scores: HouseScores,
    val comment: String? = null,
)

data class RateRoommateRequest(
    val rateeUserId: String,
    val scores: RoommateScores,
    val comment: String? = null,
)

data class RateLandlordRequest(
    val landlordUserId: String,
    val scores: LandlordScores,
    val comment: String? = null,
)

data class HouseScores(
    val cleanliness: Int,
    val amenities: Int,
    val location: Int,
    val valueForMoney: Int,
    val overall: Int,
)

data class RoommateScores(
    val communication: Int,
    val tidiness: Int,
    val respectfulness: Int,
    val socialCompatibility: Int,
    val overall: Int,
)

data class LandlordScores(
    val responsiveness: Int,
    val fairness: Int,
    val maintenanceQuality: Int,
    val overall: Int,
)

data class RatingResponse(
    val ratingId: String,
)
