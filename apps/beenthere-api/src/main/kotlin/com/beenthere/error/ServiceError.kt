package com.beenthere.error

import com.michaelbull.result.Err
import com.michaelbull.result.Ok
import com.michaelbull.result.Result
import org.springframework.http.HttpStatus

/**
 * Comprehensive error types for all services in the application
 */
sealed class ServiceError(
    open val message: String,
    val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
    val errorCode: String,
) {
    // User Service Errors
    data class UserNotFound(override val message: String = "User not found") : ServiceError(message, HttpStatus.NOT_FOUND, "USER_001")

    data class UserAlreadyExists(override val message: String = "User already exists") : ServiceError(
        message,
        HttpStatus.CONFLICT,
        "USER_002",
    )

    data class InvalidUserData(override val message: String = "Invalid user data") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "USER_003",
    )

    data class UserInactive(override val message: String = "User account is inactive") : ServiceError(
        message,
        HttpStatus.FORBIDDEN,
        "USER_004",
    )

    data class UserNotVerified(override val message: String = "User account is not verified") : ServiceError(
        message,
        HttpStatus.FORBIDDEN,
        "USER_005",
    )

    // Authentication Errors
    data class InvalidCredentials(
        override val message: String = "Invalid credentials",
    ) : ServiceError(message, HttpStatus.UNAUTHORIZED, "AUTH_001")

    data class TokenExpired(override val message: String = "Token has expired") : ServiceError(message, HttpStatus.UNAUTHORIZED, "AUTH_002")

    data class InvalidToken(override val message: String = "Invalid token") : ServiceError(message, HttpStatus.UNAUTHORIZED, "AUTH_003")

    data class TokenNotFound(override val message: String = "Token not found") : ServiceError(message, HttpStatus.UNAUTHORIZED, "AUTH_004")

    data class WeakPassword(override val message: String = "Password does not meet security requirements") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "AUTH_005",
    )

    data class AccountLocked(override val message: String = "Account is locked") : ServiceError(message, HttpStatus.FORBIDDEN, "AUTH_006")

    // Listing Service Errors
    data class ListingNotFound(
        override val message: String = "Listing not found",
    ) : ServiceError(message, HttpStatus.NOT_FOUND, "LISTING_001")

    data class ListingInactive(override val message: String = "Listing is inactive") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "LISTING_002",
    )

    data class InvalidListingData(override val message: String = "Invalid listing data") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "LISTING_003",
    )

    data class UnauthorizedListingAccess(override val message: String = "Unauthorized access to listing") : ServiceError(
        message,
        HttpStatus.FORBIDDEN,
        "LISTING_004",
    )

    data class ListingAlreadyExists(override val message: String = "Listing already exists") : ServiceError(
        message,
        HttpStatus.CONFLICT,
        "LISTING_005",
    )

    // Swipe Service Errors
    data class SwipeNotFound(override val message: String = "Swipe not found") : ServiceError(message, HttpStatus.NOT_FOUND, "SWIPE_001")

    data class DuplicateSwipe(override val message: String = "User has already swiped on this listing") : ServiceError(
        message,
        HttpStatus.CONFLICT,
        "SWIPE_002",
    )

    data class InvalidSwipeData(override val message: String = "Invalid swipe data") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "SWIPE_003",
    )

    data class SelfSwipeNotAllowed(override val message: String = "Cannot swipe on your own listing") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "SWIPE_004",
    )

    // Match Service Errors
    data class MatchNotFound(override val message: String = "Match not found") : ServiceError(message, HttpStatus.NOT_FOUND, "MATCH_001")

    data class MatchAlreadyExists(override val message: String = "Match already exists") : ServiceError(
        message,
        HttpStatus.CONFLICT,
        "MATCH_002",
    )

    data class InvalidMatchStatus(override val message: String = "Invalid match status") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "MATCH_003",
    )

    data class UnauthorizedMatchAccess(override val message: String = "Unauthorized access to match") : ServiceError(
        message,
        HttpStatus.FORBIDDEN,
        "MATCH_004",
    )

    data class MatchExpired(override val message: String = "Match has expired") : ServiceError(message, HttpStatus.BAD_REQUEST, "MATCH_005")

    // Rating Service Errors
    data class RatingNotFound(override val message: String = "Rating not found") : ServiceError(message, HttpStatus.NOT_FOUND, "RATING_001")

    data class DuplicateRating(override val message: String = "Rating already exists") : ServiceError(
        message,
        HttpStatus.CONFLICT,
        "RATING_002",
    )

    data class InvalidRatingValue(override val message: String = "Invalid rating value") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "RATING_003",
    )

    data class SelfRatingNotAllowed(override val message: String = "Cannot rate yourself") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "RATING_004",
    )

    data class UnauthorizedRatingAccess(override val message: String = "Unauthorized access to rating") : ServiceError(
        message,
        HttpStatus.FORBIDDEN,
        "RATING_005",
    )

    // Billing Service Errors
    data class PaymentNotFound(
        override val message: String = "Payment not found",
    ) : ServiceError(message, HttpStatus.NOT_FOUND, "BILLING_001")

    data class PaymentFailed(override val message: String = "Payment processing failed") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "BILLING_002",
    )

    data class InvalidPaymentAmount(override val message: String = "Invalid payment amount") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "BILLING_003",
    )

    data class RefundFailed(override val message: String = "Refund processing failed") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "BILLING_004",
    )

    data class PaymentAlreadyRefunded(override val message: String = "Payment already refunded") : ServiceError(
        message,
        HttpStatus.CONFLICT,
        "BILLING_005",
    )

    data class InsufficientFunds(override val message: String = "Insufficient funds") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "BILLING_006",
    )

    // Database Errors
    data class DatabaseConnectionError(
        override val message: String = "Database connection error",
    ) : ServiceError(message, HttpStatus.SERVICE_UNAVAILABLE, "DB_001")

    data class DatabaseConstraintViolation(override val message: String = "Database constraint violation") : ServiceError(
        message,
        HttpStatus.CONFLICT,
        "DB_002",
    )

    data class DatabaseTimeout(override val message: String = "Database operation timeout") : ServiceError(
        message,
        HttpStatus.REQUEST_TIMEOUT,
        "DB_003",
    )

    // External Service Errors
    data class ExternalServiceUnavailable(
        override val message: String = "External service unavailable",
    ) : ServiceError(message, HttpStatus.BAD_GATEWAY, "EXT_001")

    data class ExternalServiceTimeout(override val message: String = "External service timeout") : ServiceError(
        message,
        HttpStatus.GATEWAY_TIMEOUT,
        "EXT_002",
    )

    data class ExternalServiceError(override val message: String = "External service error") : ServiceError(
        message,
        HttpStatus.BAD_GATEWAY,
        "EXT_003",
    )

    // Validation Errors
    data class ValidationError(
        override val message: String = "Validation failed",
    ) : ServiceError(message, HttpStatus.BAD_REQUEST, "VAL_001")

    data class RequiredFieldMissing(override val message: String = "Required field is missing") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "VAL_002",
    )

    data class InvalidFieldFormat(override val message: String = "Invalid field format") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "VAL_003",
    )

    data class FieldTooLong(override val message: String = "Field value is too long") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "VAL_004",
    )

    data class FieldTooShort(override val message: String = "Field value is too short") : ServiceError(
        message,
        HttpStatus.BAD_REQUEST,
        "VAL_005",
    )

    // General Errors
    data class InternalServerError(
        override val message: String = "Internal server error",
    ) : ServiceError(message, HttpStatus.INTERNAL_SERVER_ERROR, "GEN_001")

    data class ResourceNotFound(override val message: String = "Resource not found") : ServiceError(
        message,
        HttpStatus.NOT_FOUND,
        "GEN_002",
    )

    data class UnauthorizedAccess(override val message: String = "Unauthorized access") : ServiceError(
        message,
        HttpStatus.UNAUTHORIZED,
        "GEN_003",
    )

    data class ForbiddenAccess(override val message: String = "Forbidden access") : ServiceError(message, HttpStatus.FORBIDDEN, "GEN_004")

    data class ConflictError(override val message: String = "Resource conflict") : ServiceError(message, HttpStatus.CONFLICT, "GEN_005")

    data class ServiceUnavailable(override val message: String = "Service unavailable") : ServiceError(
        message,
        HttpStatus.SERVICE_UNAVAILABLE,
        "GEN_006",
    )
}

fun <T> Result<T, ServiceError>.toApiResponse(): com.beenthere.common.ApiResponse<T> {
    return when (this) {
        is Ok -> com.beenthere.common.ApiResponse.success(this.value)
        is Err ->
            com.beenthere.common.ApiResponse.error(
                com.beenthere.common.AppError.InternalError(this.error.message),
            )
    }
}

object ServiceErrorFactory {
    fun userNotFound(message: String = "User not found"): Result<Nothing, ServiceError> = Err(ServiceError.UserNotFound(message))

    fun userAlreadyExists(message: String = "User already exists"): Result<Nothing, ServiceError> =
        Err(ServiceError.UserAlreadyExists(message))

    fun invalidCredentials(message: String = "Invalid credentials"): Result<Nothing, ServiceError> =
        Err(ServiceError.InvalidCredentials(message))

    fun listingNotFound(message: String = "Listing not found"): Result<Nothing, ServiceError> = Err(ServiceError.ListingNotFound(message))

    fun unauthorizedAccess(message: String = "Unauthorized access"): Result<Nothing, ServiceError> =
        Err(ServiceError.UnauthorizedAccess(message))

    fun validationError(message: String = "Validation failed"): Result<Nothing, ServiceError> = Err(ServiceError.ValidationError(message))

    fun internalError(message: String = "Internal server error"): Result<Nothing, ServiceError> =
        Err(ServiceError.InternalServerError(message))
}
