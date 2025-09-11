package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.entities.PlaceEntity
import com.beenthere.repositories.PlaceRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.michaelbull.result.*
import org.springframework.stereotype.Service
import java.util.*

data class PlaceRef(
    val googlePlaceId: String? = null,
    val formattedAddress: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
)

@Service
class PlaceService(
    private val placeRepository: PlaceRepository,
    private val objectMapper: ObjectMapper
) {
    
    /**
     * Get or create a place from PlaceRef.
     * Used by rant creation to ensure place exists.
     */
    suspend fun getOrCreatePlace(placeRef: PlaceRef): Result<PlaceEntity, ServiceError> {
        // Validate input
        if (placeRef.googlePlaceId.isNullOrBlank() && (placeRef.lat == null || placeRef.lng == null)) {
            return Err(ServiceError.ValidationError("place", "Must provide either googlePlaceId or both lat/lng coordinates"))
        }
        
        return try {
            // Try to find existing place by Google Place ID
            if (!placeRef.googlePlaceId.isNullOrBlank()) {
                placeRepository.findByGooglePlaceId(placeRef.googlePlaceId)?.let { existing ->
                    return Ok(existing)
                }
            }
            
            // Try to find by coordinates if available
            if (placeRef.lat != null && placeRef.lng != null) {
                placeRepository.findByCoordinatesWithTolerance(placeRef.lat, placeRef.lng)?.let { existing ->
                    return Ok(existing)
                }
            }
            
            // Create new place
            val newPlace = PlaceEntity(
                googlePlaceId = placeRef.googlePlaceId,
                formattedAddress = placeRef.formattedAddress,
                lat = placeRef.lat,
                lng = placeRef.lng,
                attrs = null // No additional attributes in MVP
            )
            
            val savedPlace = placeRepository.save(newPlace)
            Ok(savedPlace)
            
        } catch (e: Exception) {
            Err(ServiceError.DatabaseError("Failed to get or create place", e))
        }
    }
    
    /**
     * Get place by ID.
     */
    suspend fun getPlaceById(id: UUID): Result<PlaceEntity, ServiceError> {
        return try {
            val place = placeRepository.findById(id)
            if (place != null) {
                Ok(place)
            } else {
                Err(ServiceError.NotFound("place", id.toString()))
            }
        } catch (e: Exception) {
            Err(ServiceError.DatabaseError("Failed to get place", e))
        }
    }
}