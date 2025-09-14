package com.beenthere.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Mono
import kotlinx.coroutines.reactor.awaitSingleOrNull

/**
 * Extension functions for working with Kotlin Result in Spring WebFlux
 */

/**
 * Convert a Result to a Mono
 */
fun <T> Result<T>.toMono(): Mono<T> = fold(
    onSuccess = { value -> if (value != null) Mono.just(value) else Mono.empty() },
    onFailure = { Mono.error(it) }
)

/**
 * Convert a Mono to a Result using coroutines
 */
suspend fun <T> Mono<T>.toResult(): Result<T> = try {
    val result = this.awaitSingleOrNull()
    if (result != null) Result.success(result) else Result.failure(RuntimeException("Mono was empty"))
} catch (e: Exception) {
    Result.failure(e)
}

/**
 * Map ServiceError to appropriate HTTP status and response
 */
fun ServiceError.toResponseEntity(): ResponseEntity<ErrorResponse> {
    val status = when (this) {
        is ServiceError.PlaceNotFound -> HttpStatus.NOT_FOUND
        is ServiceError.UserNotFound -> HttpStatus.NOT_FOUND
        is ServiceError.ListingNotFound -> HttpStatus.NOT_FOUND
        is ServiceError.MemberNotFound -> HttpStatus.NOT_FOUND
        is ServiceError.ValidationError -> HttpStatus.BAD_REQUEST
        is ServiceError.PhoneValidationError -> HttpStatus.BAD_REQUEST
        is ServiceError.BadRequest -> HttpStatus.BAD_REQUEST
        is ServiceError.InvalidGoogleToken -> HttpStatus.UNAUTHORIZED
        is ServiceError.Forbidden -> HttpStatus.FORBIDDEN
        is ServiceError.RateLimitExceeded -> HttpStatus.TOO_MANY_REQUESTS
        is ServiceError.PlaceLookupFailed -> HttpStatus.BAD_GATEWAY
        is ServiceError.ExternalServiceError -> HttpStatus.BAD_GATEWAY
        is ServiceError.DatabaseError -> HttpStatus.INTERNAL_SERVER_ERROR
    }

    return ResponseEntity.status(status).body(
        ErrorResponse(
            code = this.code,
            message = this.message,
            timestamp = java.time.Instant.now().toString()
        )
    )
}

/**
 * Error response DTO
 */
data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: String
)

/**
 * Extension to convert Result to ResponseEntity for controllers
 */
fun <T> Result<T>.toResponseEntity(
    successStatus: HttpStatus = HttpStatus.OK
): ResponseEntity<*> = fold(
    onSuccess = { ResponseEntity.status(successStatus).body(it) },
    onFailure = { ex ->
        when (ex) {
            is ServiceError -> ex.toResponseEntity()
            else -> ServiceError.DatabaseError(ex).toResponseEntity()
        }
    }
)