package com.beenthere.swipe

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.swipe.SwipeResponse
import com.beenthere.error.ServiceError
import com.beenthere.error.toApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import jakarta.validation.Valid

@RestController
@RequestMapping("/api/v1")
class SwipeController(
    private val swipeService: SwipeService
) {

    @PostMapping("/swipes")
    fun createSwipe(@Valid @RequestBody request: CreateSwipeRequest): Mono<ResponseEntity<ApiResponse<SwipeResponse>>> {
        return swipeService.createSwipe(request)
            .map { result ->
                result.fold(
                    success = { swipeResponse ->
                        ApiResponse.success(swipeResponse).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<SwipeResponse>().toResponseEntity()
                    }
                )
            }
    }
}
