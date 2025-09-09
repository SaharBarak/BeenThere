package com.beenthere.swipe

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.toApiResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1")
class SwipeController(
    private val swipeService: SwipeService,
) {
    @PostMapping("/swipes")
    fun createSwipe(
        @Valid @RequestBody request: CreateSwipeRequest,
    ): Mono<ResponseEntity<ApiResponse<SwipeResponse>>> {
        return swipeService.createSwipe(request)
            .map { result ->
                result.fold(
                    onSuccess = { swipeResponse ->
                        ApiResponse.success(swipeResponse).toResponseEntity()
                    },
                    onFailure = { error ->
                        error.toApiResponse<SwipeResponse>().toResponseEntity()
                    },
                )
            }
    }
}
