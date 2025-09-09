package com.beenthere.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(AppError::class)
    fun handleAppError(
        ex: AppError,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ApiResponse<Nothing>>> {
        val response = ApiResponse.error<Nothing>(ex)
        return Mono.just(ResponseEntity.status(ex.httpStatus).body(response))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): Mono<ResponseEntity<ApiResponse<Nothing>>> {
        val errors =
            ex.bindingResult.fieldErrors.associate { fieldError ->
                fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
            }

        val errorDetails =
            ErrorDetails(
                code = HttpStatus.BAD_REQUEST.value(),
                message = "Validation failed",
                type = "ValidationError",
                details = errors,
            )

        val response =
            ApiResponse<Nothing>(
                success = false,
                error = errorDetails,
                timestamp = Instant.now(),
            )

        return Mono.just(ResponseEntity.badRequest().body(response))
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): Mono<ResponseEntity<ApiResponse<Nothing>>> {
        val response =
            ApiResponse.error<Nothing>(
                AppError.UnauthorizedError(ex.message ?: "Authentication failed"),
            )
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Mono<ResponseEntity<ApiResponse<Nothing>>> {
        val response =
            ApiResponse.error<Nothing>(
                AppError.ValidationError(ex.message ?: "Invalid argument"),
            )
        return Mono.just(ResponseEntity.badRequest().body(response))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException): Mono<ResponseEntity<ApiResponse<Nothing>>> {
        val response =
            ApiResponse.error<Nothing>(
                AppError.ConflictError(ex.message ?: "Invalid state"),
            )
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(response))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): Mono<ResponseEntity<ApiResponse<Nothing>>> {
        val response =
            ApiResponse.error<Nothing>(
                AppError.NotFoundError(ex.message ?: "Resource not found"),
            )
        return Mono.just(ResponseEntity.notFound().build())
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): Mono<ResponseEntity<ApiResponse<Nothing>>> {
        val response =
            ApiResponse.error<Nothing>(
                AppError.InternalError("An unexpected error occurred: ${ex.message}"),
            )
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response))
    }
}
