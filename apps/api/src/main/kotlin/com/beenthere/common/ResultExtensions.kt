package com.beenthere.common

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

/**
 * Extensions for Result monad integration with Spring WebFlux.
 * Maps service layer Result<T, ServiceError> to HTTP responses.
 */

/**
 * Convert ServiceError to appropriate HTTP status code
 */
fun ServiceError.toHttpStatus(): HttpStatus = when (this) {
    is ServiceError.ValidationError,
    is ServiceError.InvalidInput -> HttpStatus.BAD_REQUEST
    
    is ServiceError.AuthenticationRequired,
    is ServiceError.InvalidCredentials -> HttpStatus.UNAUTHORIZED
    
    is ServiceError.InsufficientPermissions,
    is ServiceError.SubscriptionRequired -> HttpStatus.FORBIDDEN
    
    is ServiceError.NotFound -> HttpStatus.NOT_FOUND
    
    is ServiceError.AlreadyExists,
    is ServiceError.ConcurrentModification -> HttpStatus.CONFLICT
    
    is ServiceError.BusinessRuleViolation -> HttpStatus.UNPROCESSABLE_ENTITY
    
    is ServiceError.RateLimitExceeded -> HttpStatus.TOO_MANY_REQUESTS
    
    is ServiceError.DatabaseError,
    is ServiceError.ExternalServiceError,
    is ServiceError.InternalError -> HttpStatus.INTERNAL_SERVER_ERROR
}

/**
 * Error response body for API consistency
 */
data class ErrorResponse(
    val error: String,
    val message: String,
    val code: String,
    val timestamp: String = java.time.Instant.now().toString()
)

/**
 * Convert ServiceError to ErrorResponse
 */
fun ServiceError.toErrorResponse(): ErrorResponse = ErrorResponse(
    error = this.toHttpStatus().reasonPhrase,
    message = this.message,
    code = this.code
)

/**
 * Convert Result<T, ServiceError> to ResponseEntity<T>
 * Usage: service.doSomething().toResponseEntity()
 */
fun <T> Result<T, ServiceError>.toResponseEntity(): ResponseEntity<*> = when {
    this.isOk -> ResponseEntity.ok(this.value)
    else -> {
        val error = this.error
        ResponseEntity
            .status(error.toHttpStatus())
            .body(error.toErrorResponse())
    }
}

/**
 * Convert Result<T, ServiceError> to Mono<ResponseEntity<*>>
 * Usage in WebFlux controllers: service.doSomething().toMonoResponse()
 */
fun <T> Result<T, ServiceError>.toMonoResponse(): Mono<ResponseEntity<*>> = 
    Mono.just(this.toResponseEntity())

/**
 * Convert Result<T, ServiceError> to Mono<ServerResponse>
 * Usage in functional routing: service.doSomething().toServerResponse()
 */
fun <T: Any> Result<T, ServiceError>.toServerResponse(): Mono<ServerResponse> = when {
    this.isOk -> ServerResponse.ok().bodyValue(this.value as Any)
    else -> {
        val error = this.error
        ServerResponse
            .status(error.toHttpStatus())
            .bodyValue(error.toErrorResponse())
    }
}

/**
 * Wrap exceptions as ServiceError.InternalError
 */
fun <T> Result<T, Throwable>.mapToServiceError(): Result<T, ServiceError> =
    this.mapError { throwable ->
        when (throwable) {
            is ServiceError -> throwable
            else -> ServiceError.InternalError("Unexpected error: ${throwable.message}")
        }
    }