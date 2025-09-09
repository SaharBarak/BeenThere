package com.beenthere.common

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.Instant

/**
 * Standard API response wrapper for consistent response format
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetails? = null,
    val timestamp: Instant = Instant.now()
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(
            success = true,
            data = data
        )
        
        fun <T> error(error: AppError): ApiResponse<T> = ApiResponse(
            success = false,
            error = ErrorDetails(
                code = error.httpStatus.value(),
                message = error.message,
                type = error::class.simpleName ?: "UnknownError"
            )
        )
        
        fun <T> error(message: String, status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR): ApiResponse<T> = ApiResponse(
            success = false,
            error = ErrorDetails(
                code = status.value(),
                message = message,
                type = "Error"
            )
        )
    }
}

data class ErrorDetails(
    val code: Int,
    val message: String,
    val type: String,
    val details: Map<String, Any>? = null
)

/**
 * Extension function to convert ApiResponse to ResponseEntity
 */
fun <T> ApiResponse<T>.toResponseEntity(): ResponseEntity<ApiResponse<T>> {
    val status = if (success) HttpStatus.OK else HttpStatus.valueOf(error?.code ?: 500)
    return ResponseEntity.status(status).body(this)
}
