package com.beenthere.accessors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import jakarta.annotation.PostConstruct

/**
 * S3 Accessor using AWS SDK for Kotlin
 */
@Component
class S3Accessor(
    @Value("\${beenthere.aws.access-key-id}")
    private val accessKeyId: String,
    @Value("\${beenthere.aws.secret-access-key}")
    private val secretAccessKey: String,
    @Value("\${beenthere.aws.region:us-east-1}")
    private val region: String,
    @Value("\${beenthere.s3.bucket}")
    private val bucketName: String
) {
    
    private lateinit var s3Client: S3AsyncClient
    
    @PostConstruct
    fun init() {
        val credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey)
        s3Client = S3AsyncClient.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build()
    }
    
    suspend fun uploadFile(
        key: String,
        contentType: String,
        data: ByteArray
    ): String = withContext(Dispatchers.IO) {
        val putRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(contentType)
            .build()
        
        s3Client.putObject(putRequest, AsyncRequestBody.fromBytes(data)).await()
        generateFileUrl(key)
    }
    
    suspend fun downloadFile(key: String): ByteArray = withContext(Dispatchers.IO) {
        val getRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build()
        
        val response = s3Client.getObject(getRequest, AsyncResponseTransformer.toBytes()).await()
        response.asByteArray()
    }
    
    suspend fun deleteFile(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
            
            s3Client.deleteObject(deleteRequest).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun fileExists(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build()
            
            s3Client.headObject(headRequest).await()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    private fun generateFileUrl(key: String): String {
        return "https://$bucketName.s3.amazonaws.com/$key"
    }
}