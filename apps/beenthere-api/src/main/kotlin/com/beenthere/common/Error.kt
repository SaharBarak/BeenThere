package com.beenthere.common

import org.springframework.http.HttpStatus

/**
 * Represents different types of errors that can occur in the application
 */
sealed class AppError(
    open val message: String,
    val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
) {
    data class ValidationError(override val message: String) : AppError(message, HttpStatus.BAD_REQUEST)

    data class NotFoundError(override val message: String) : AppError(message, HttpStatus.NOT_FOUND)

    data class UnauthorizedError(override val message: String) : AppError(message, HttpStatus.UNAUTHORIZED)

    data class ForbiddenError(override val message: String) : AppError(message, HttpStatus.FORBIDDEN)

    data class ConflictError(override val message: String) : AppError(message, HttpStatus.CONFLICT)

    data class InternalError(override val message: String) : AppError(message, HttpStatus.INTERNAL_SERVER_ERROR)

    data class ExternalServiceError(override val message: String) : AppError(message, HttpStatus.BAD_GATEWAY)
}

// Temporarily commented out Result monad usage
// fun <T> Result<T, AppError>.toResponse(): ApiResponse<T> {
//     return when (this) {
//         is Ok -> ApiResponse.success(this.value)
//         is Err -> ApiResponse.error(this.error)
//     }
// }

// object ErrorFactory {
//     fun validationError(message: String): Result<Nothing, AppError> =
//         Err(AppError.ValidationError(message))
//
//     fun notFoundError(message: String): Result<Nothing, AppError> =
//         Err(AppError.NotFoundError(message))
//
//     fun unauthorizedError(message: String): Result<Nothing, AppError> =
//         Err(AppError.UnauthorizedError(message))
//
//     fun forbiddenError(message: String): Result<Nothing, AppError> =
//         Err(AppError.ForbiddenError(message))
//
//     fun conflictError(message: String): Result<Nothing, AppError> =
//         Err(AppError.ConflictError(message))
//
//     fun internalError(message: String): Result<Nothing, AppError> =
//         Err(AppError.InternalError(message))
//
//     fun externalServiceError(message: String): Result<Nothing, AppError> =
//         Err(AppError.ExternalServiceError(message))
// }
