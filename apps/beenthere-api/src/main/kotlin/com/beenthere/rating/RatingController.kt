package com.beenthere.rating

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.toApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/ratings")
class RatingController(
    private val ratingService: RatingService,
) {
    @PostMapping("/house")
    fun rateHouse(
        @Valid @RequestBody request: RateHouseRequest,
    ): Mono<ResponseEntity<ApiResponse<RatingResponse>>> {
        return ratingService.rateHouse(request)
            .map { result ->
                result.fold(
                    success = { rating ->
                        ApiResponse.success(RatingResponse(rating.id)).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<RatingResponse>().toResponseEntity()
                    },
                )
            }
    }

    @PostMapping("/roommate")
    fun rateRoommate(
        @Valid @RequestBody request: RateRoommateRequest,
    ): Mono<ResponseEntity<ApiResponse<RatingResponse>>> {
        return ratingService.rateRoommate(request)
            .map { result ->
                result.fold(
                    success = { rating ->
                        ApiResponse.success(RatingResponse(rating.id)).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<RatingResponse>().toResponseEntity()
                    },
                )
            }
    }

    @PostMapping("/landlord")
    fun rateLandlord(
        @Valid @RequestBody request: RateLandlordRequest,
    ): Mono<ResponseEntity<ApiResponse<RatingResponse>>> {
        return ratingService.rateLandlord(request)
            .map { result ->
                result.fold(
                    success = { rating ->
                        ApiResponse.success(RatingResponse(rating.id)).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<RatingResponse>().toResponseEntity()
                    },
                )
            }
    }
}
