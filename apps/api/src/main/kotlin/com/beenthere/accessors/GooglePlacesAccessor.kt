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
 * Google Places API Accessor using Retrofit
 */
@Component
class GooglePlacesAccessor(
    @Value("\${beenthere.google.places.api-key}")
    private val apiKey: String
) {
    
    private lateinit var placesApi: GooglePlacesApi
    
    @PostConstruct
    fun init() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
        
        placesApi = retrofit.create(GooglePlacesApi::class.java)
    }
    
    suspend fun searchPlaces(query: String): GooglePlacesResponse? = withContext(Dispatchers.IO) {
        try {
            val response = placesApi.searchPlaces(
                query = query,
                key = apiKey
            )
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getPlaceDetails(placeId: String): GooglePlaceDetails? = withContext(Dispatchers.IO) {
        try {
            val response = placesApi.getPlaceDetails(
                placeId = placeId,
                fields = "place_id,formatted_address,geometry,name,types",
                key = apiKey
            )
            if (response.isSuccessful) {
                response.body()?.result
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

interface GooglePlacesApi {
    @GET("textsearch/json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("key") key: String
    ): Response<GooglePlacesResponse>
    
    @GET("details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String,
        @Query("key") key: String
    ): Response<GooglePlaceDetailsResponse>
}

data class GooglePlacesResponse(
    val results: List<GooglePlaceDetails>,
    val status: String
)

data class GooglePlaceDetailsResponse(
    val result: GooglePlaceDetails,
    val status: String
)

data class GooglePlaceDetails(
    val place_id: String,
    val formatted_address: String?,
    val name: String?,
    val geometry: GooglePlaceGeometry?,
    val types: List<String>?
)

data class GooglePlaceGeometry(
    val location: GooglePlaceLocation
)

data class GooglePlaceLocation(
    val lat: Double,
    val lng: Double
)