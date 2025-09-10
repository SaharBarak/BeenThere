package com.beenthere.common

/**
 * Service layer error types for Result monad pattern.
 * Maps cleanly to HTTP status codes.
 */
sealed class ServiceError(val message: String, val code: String) {
    
    // 400 Bad Request
    data class ValidationError(val field: String, val reason: String) : 
        ServiceError("Validation failed for field '$field': $reason", "VALIDATION_ERROR")
    
    data class InvalidInput(val details: String) : 
        ServiceError("Invalid input: $details", "INVALID_INPUT")
    
    // 401 Unauthorized
    data class AuthenticationRequired(val details: String = "Authentication required") : 
        ServiceError(details, "AUTHENTICATION_REQUIRED")
    
    data class InvalidCredentials(val details: String = "Invalid credentials") : 
        ServiceError(details, "INVALID_CREDENTIALS")
    
    // 403 Forbidden
    data class InsufficientPermissions(val details: String) : 
        ServiceError("Insufficient permissions: $details", "INSUFFICIENT_PERMISSIONS")
    
    data class SubscriptionRequired(val feature: String) : 
        ServiceError("Active subscription required for feature: $feature", "SUBSCRIPTION_REQUIRED")
    
    // 404 Not Found
    data class NotFound(val resource: String, val id: String) : 
        ServiceError("$resource with id '$id' not found", "NOT_FOUND")
    
    // 409 Conflict
    data class AlreadyExists(val resource: String, val field: String, val value: String) : 
        ServiceError("$resource with $field '$value' already exists", "ALREADY_EXISTS")
    
    data class ConcurrentModification(val resource: String, val id: String) : 
        ServiceError("$resource '$id' was modified by another operation", "CONCURRENT_MODIFICATION")
    
    // 422 Unprocessable Entity
    data class BusinessRuleViolation(val rule: String, val details: String) : 
        ServiceError("Business rule violation '$rule': $details", "BUSINESS_RULE_VIOLATION")
    
    // 429 Too Many Requests
    data class RateLimitExceeded(val resource: String, val limit: String) : 
        ServiceError("Rate limit exceeded for $resource: $limit", "RATE_LIMIT_EXCEEDED")
    
    // 500 Internal Server Error
    data class DatabaseError(val operation: String, val details: String) : 
        ServiceError("Database error during $operation: $details", "DATABASE_ERROR")
    
    data class ExternalServiceError(val service: String, val details: String) : 
        ServiceError("External service error ($service): $details", "EXTERNAL_SERVICE_ERROR")
    
    data class InternalError(val details: String) : 
        ServiceError("Internal error: $details", "INTERNAL_ERROR")
}