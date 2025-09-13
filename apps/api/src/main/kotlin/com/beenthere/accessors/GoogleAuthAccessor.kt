package com.beenthere.accessors

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import jakarta.annotation.PostConstruct

/**
 * Google Auth Accessor using Google OAuth2 API via Retrofit
 */
@Component
class GoogleAuthAccessor(
    @Value("\${beenthere.google.client-id}")
    private val googleClientId: String
) {
    
    private lateinit var googleApi: GoogleAuthApi
    
    @PostConstruct
    fun init() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/oauth2/v3/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
        
        googleApi = retrofit.create(GoogleAuthApi::class.java)
    }
    
    suspend fun verifyIdToken(idToken: String): GoogleUserInfo? = withContext(Dispatchers.IO) {
        try {
            val response = googleApi.verifyToken(idToken)
            if (response.isSuccessful) {
                val tokenInfo = response.body()
                if (tokenInfo != null && tokenInfo.aud == googleClientId) {
                    GoogleUserInfo(
                        googleSub = tokenInfo.sub,
                        email = tokenInfo.email,
                        emailVerified = tokenInfo.email_verified == "true",
                        name = tokenInfo.name,
                        pictureUrl = tokenInfo.picture,
                        givenName = tokenInfo.given_name,
                        familyName = tokenInfo.family_name,
                        locale = tokenInfo.locale
                    )
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun isValidToken(idToken: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = googleApi.verifyToken(idToken)
            response.isSuccessful && response.body()?.aud == googleClientId
        } catch (e: Exception) {
            false
        }
    }
}

interface GoogleAuthApi {
    @GET("tokeninfo")
    suspend fun verifyToken(@Query("id_token") idToken: String): Response<GoogleTokenInfo>
}

data class GoogleTokenInfo(
    val aud: String,
    val sub: String,
    val email: String,
    val email_verified: String,
    val name: String?,
    val picture: String?,
    val given_name: String?,
    val family_name: String?,
    val locale: String?
)

/**
 * Data class representing Google user information from ID token
 */
data class GoogleUserInfo(
    val googleSub: String,
    val email: String,
    val emailVerified: Boolean,
    val name: String?,
    val pictureUrl: String?,
    val givenName: String?,
    val familyName: String?,
    val locale: String?
)