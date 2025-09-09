package com.beenthere.match

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponse
import com.beenthere.common.toResponseEntity
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/matches")
class MatchController(
    private val matchService: MatchService
) {

    @GetMapping("/my-matches")
    fun getMyMatches(@AuthenticationPrincipal userId: String): Mono<ResponseEntity<ApiResponse<List<MatchDto>>>> {
        return matchService.getMatchesByUser(userId)
            .map { it.toDto() }
            .collectList()
            .map { matches ->
                ResponseEntity.ok(ApiResponse.success(matches))
            }
    }

    @GetMapping("/active")
    fun getActiveMatches(@AuthenticationPrincipal userId: String): Mono<ResponseEntity<ApiResponse<List<MatchDto>>>> {
        return matchService.getActiveMatches(userId)
            .map { it.toDto() }
            .collectList()
            .map { matches ->
                ResponseEntity.ok(ApiResponse.success(matches))
            }
    }

    @GetMapping("/{id}")
    fun getMatchById(@PathVariable id: String): Mono<ResponseEntity<ApiResponse<MatchDto>>> {
        return matchService.getMatchesByUser(id)
            .next()
            .map { match ->
                ApiResponse.success(match.toDto()).toResponseEntity()
            }
            .onErrorReturn(ApiResponse.error<MatchDto>("Match not found").toResponseEntity())
    }

    @PutMapping("/{id}/status")
    fun updateMatchStatus(
        @PathVariable id: String,
        @AuthenticationPrincipal userId: String,
        @RequestBody request: UpdateMatchStatusRequest
    ): Mono<ResponseEntity<ApiResponse<MatchDto>>> {
        return matchService.updateMatchStatus(id, request.status)
            .map { result ->
                result.fold(
                    success = { match ->
                        ApiResponse.success(match.toDto()).toResponseEntity()
                    },
                    failure = { error ->
                        ApiResponse.error<MatchDto>("Failed to update match status").toResponseEntity()
                    }
                )
            }
    }

    @GetMapping("/stats")
    fun getMatchStats(@AuthenticationPrincipal userId: String): Mono<ResponseEntity<ApiResponse<MatchStats>>> {
        return matchService.getMatchStats(userId)
            .map { stats ->
                ApiResponse.success(stats).toResponseEntity()
            }
    }
}

data class UpdateMatchStatusRequest(
    val status: String
)

data class MatchDto(
    val id: String,
    val userId: String,
    val landlordId: String,
    val listingId: String,
    val status: String,
    val createdAt: java.time.Instant,
    val updatedAt: java.time.Instant
)

fun MatchEntity.toDto(): MatchDto = MatchDto(
    id = this.id,
    userId = this.userId,
    landlordId = this.landlordId,
    listingId = this.listingId,
    status = this.status,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
