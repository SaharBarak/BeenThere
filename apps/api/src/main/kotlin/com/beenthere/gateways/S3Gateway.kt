package com.beenthere.gateways

import com.beenthere.accessors.S3Accessor
import org.springframework.stereotype.Component
import java.util.*

/**
 * S3 Gateway - Domain interface for S3 operations
 * Wraps S3Accessor and provides domain-specific methods for file handling
 */
@Component
class S3Gateway(
    private val s3Accessor: S3Accessor
) {
    
    /**
     * Upload apartment listing photo
     */
    suspend fun uploadApartmentPhoto(
        listingId: String,
        imageData: ByteArray,
        contentType: String
    ): Result<String> {
        return try {
            val key = "listings/$listingId/photos/${UUID.randomUUID()}.jpg"
            val url = s3Accessor.uploadFile(key, contentType, imageData)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception("S3 upload failed: ${e.message}", e))
        }
    }
    
    /**
     * Upload user profile photo
     */
    suspend fun uploadUserPhoto(
        userId: String,
        imageData: ByteArray,
        contentType: String
    ): Result<String> {
        return try {
            val key = "users/$userId/profile/${UUID.randomUUID()}.jpg"
            val url = s3Accessor.uploadFile(key, contentType, imageData)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(Exception("S3 upload failed: ${e.message}", e))
        }
    }
    
    /**
     * Delete a file from S3
     */
    suspend fun deleteFile(fileUrl: String): Result<Boolean> {
        return try {
            // Extract key from URL
            val key = fileUrl.substringAfterLast(".amazonaws.com/")
            val deleted = s3Accessor.deleteFile(key)
            Result.success(deleted)
        } catch (e: Exception) {
            Result.failure(Exception("S3 delete failed: ${e.message}", e))
        }
    }
    
    /**
     * Check if file exists
     */
    suspend fun fileExists(fileUrl: String): Result<Boolean> {
        return try {
            // Extract key from URL
            val key = fileUrl.substringAfterLast(".amazonaws.com/")
            val exists = s3Accessor.fileExists(key)
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(Exception("S3 check failed: ${e.message}", e))
        }
    }
}