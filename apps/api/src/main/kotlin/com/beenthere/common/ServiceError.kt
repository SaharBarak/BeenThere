package com.beenthere.common

sealed class ServiceError(
    override val message: String,
    val code: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    // Place-related errors
    data class PlaceNotFound(val placeId: String) : ServiceError(
        message = "Place not found: $placeId",
        code = "PLACE_NOT_FOUND"
    )

    data class PlaceLookupFailed(val googlePlaceId: String, private val errorCause: Throwable) : ServiceError(
        message = "Failed to lookup place: $googlePlaceId",
        code = "PLACE_LOOKUP_FAILED",
        cause = errorCause
    )

    // User-related errors
    data class UserNotFound(val userId: String) : ServiceError(
        message = "User not found: $userId",
        code = "USER_NOT_FOUND"
    )

    data class InvalidGoogleToken(private val errorCause: Throwable? = null) : ServiceError(
        message = "Invalid Google ID token",
        code = "INVALID_GOOGLE_TOKEN",
        cause = errorCause
    )

    // Validation errors
    data class ValidationError(val field: String, val reason: String) : ServiceError(
        message = "Validation failed for field '$field': $reason",
        code = "VALIDATION_ERROR"
    )

    data class PhoneValidationError(val phone: String) : ServiceError(
        message = "Invalid phone number format: $phone",
        code = "PHONE_VALIDATION_ERROR"
    )

    // Rate limiting
    data object RateLimitExceeded : ServiceError(
        message = "Rate limit exceeded",
        code = "RATE_LIMIT_EXCEEDED"
    )

    // Generic errors
    data class DatabaseError(private val errorCause: Throwable) : ServiceError(
        message = "Database operation failed",
        code = "DATABASE_ERROR",
        cause = errorCause
    )

    data class ExternalServiceError(val service: String, private val errorCause: Throwable) : ServiceError(
        message = "External service error: $service",
        code = "EXTERNAL_SERVICE_ERROR",
        cause = errorCause
    )
}