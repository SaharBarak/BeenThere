package com.beenthere.feed

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.toApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/listings")
class FeedController(
    private val feedService: FeedService,
) {
    @GetMapping("/feed")
    fun getFeed(
        @RequestParam mode: String, // "roommates" or "houses"
        @RequestParam(required = false) cursor: String?,
    ): Mono<ResponseEntity<ApiResponse<FeedResponse>>> {
        return feedService.getFeed(mode, cursor)
            .map { result ->
                result.fold(
                    success = { feed ->
                        ApiResponse.success(feed).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<FeedResponse>().toResponseEntity()
                    },
                )
            }
    }
}
