package com.beenthere.services

import com.beenthere.common.ServiceError
import com.beenthere.dto.common.PlaceRef
import com.beenthere.dto.places.*
import com.beenthere.entities.PlaceEntity
// import com.beenthere.gateways.GooglePlacesGateway // Not used for now
import com.beenthere.repositories.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class PlaceService(
    // private val googlePlacesGateway: GooglePlacesGateway, // Not used for now
    private val placeRepository: PlaceRepository,
    private val rantGroupRepository: RantGroupRepository,
    private val ratingLandlordRepository: RatingLandlordRepository,
    private val ratingApartmentRepository: RatingApartmentRepository,
    private val objectMapper: ObjectMapper
) {
    
    @Transactional
    suspend fun snapPlace(req: PlaceSnapReq): Result<PlaceSnapRes> {
        return try {
            // First check if place already exists by googlePlaceId
            if (req.googlePlaceId != null) {
                val existingPlace = placeRepository.findByGooglePlaceId(req.googlePlaceId)
                if (existingPlace != null) {
                    return Result.success(PlaceSnapRes(placeId = existingPlace.id.toString()))
                }
            }
            
            // If we have coordinates, check for existing place nearby
            if (req.lat != null && req.lng != null) {
                val nearbyPlace = placeRepository.findNearbyPlace(req.lat, req.lng, 0.001) // ~100m radius
                if (nearbyPlace != null) {
                    return Result.success(PlaceSnapRes(placeId = nearbyPlace.id.toString()))
                }
            }
            
            // Create new place
            val newPlace = PlaceEntity(
                googlePlaceId = req.googlePlaceId,
                formattedAddress = req.formattedAddress,
                lat = req.lat,
                lng = req.lng,
                attrs = null // No additional attributes for now
            )
            
            val savedPlace = placeRepository.save(newPlace)
            Result.success(PlaceSnapRes(placeId = savedPlace.id.toString()))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    suspend fun getPlaceProfile(placeId: UUID): Result<PlaceProfileRes> {
        return try {
            // Get place
            val place = placeRepository.findById(placeId)
                ?: return Result.failure(ServiceError.PlaceNotFound(placeId.toString()))
            
            // Get all rant groups for this place
            val rantGroups = rantGroupRepository.findByPlaceId(placeId)
            
            // Get ratings for these rant groups
            val landlordRatings = ratingLandlordRepository.findByRantGroupIdIn(rantGroups.map { it.id!! })
            val apartmentRatings = ratingApartmentRepository.findByRantGroupIdIn(rantGroups.map { it.id!! })
            
            // Calculate counts
            val landlordCount = landlordRatings.size
            val apartmentCount = apartmentRatings.size
            
            // Calculate averages
            val landlordAverage = if (landlordCount > 0) {
                landlordRatings.map { rating ->
                    val scores = objectMapper.convertValue(rating.scores, LandlordScores::class.java)
                    (scores.fairness + scores.response + scores.maintenance + scores.privacy) / 4.0
                }.average()
            } else null
            
            val apartmentAverage = if (apartmentCount > 0) {
                apartmentRatings.map { rating ->
                    val scores = objectMapper.convertValue(rating.scores, ApartmentScores::class.java)
                    (scores.condition + scores.noise + scores.utilities + scores.sunlightMold) / 4.0
                }.average()
            } else null
            
            // Calculate extras averages
            val extrasAverages = mutableMapOf<String, Double>()
            apartmentRatings.forEach { rating ->
                if (rating.extras != null) {
                    val extras = objectMapper.convertValue(rating.extras, Extras::class.java)
                    extras.neighborsNoise?.let { addToAverage(extrasAverages, "neighborsNoise", it.toDouble()) }
                    extras.roofCommon?.let { addToAverage(extrasAverages, "roofCommon", it.toDouble()) }
                    extras.elevatorSolar?.let { addToAverage(extrasAverages, "elevatorSolar", it.toDouble()) }
                    extras.neighSafety?.let { addToAverage(extrasAverages, "neighSafety", it.toDouble()) }
                    extras.neighServices?.let { addToAverage(extrasAverages, "neighServices", it.toDouble()) }
                    extras.neighTransit?.let { addToAverage(extrasAverages, "neighTransit", it.toDouble()) }
                    extras.priceFairness?.let { addToAverage(extrasAverages, "priceFairness", it.toDouble()) }
                }
            }
            
            // Get recent ratings (last 10)
            val recentRantGroups = rantGroups.sortedByDescending { it.createdAt }.take(10)
            val recentRatings = recentRantGroups.map { rantGroup ->
                val landlordRating = landlordRatings.find { it.rantGroupId == rantGroup.id }
                val apartmentRating = apartmentRatings.find { it.rantGroupId == rantGroup.id }
                
                RecentRating(
                    at = rantGroup.createdAt.toString(),
                    landlordScores = landlordRating?.let { 
                        objectMapper.convertValue(it.scores, LandlordScores::class.java)
                    },
                    apartmentScores = apartmentRating?.let {
                        objectMapper.convertValue(it.scores, ApartmentScores::class.java)
                    },
                    extras = apartmentRating?.extras?.let {
                        objectMapper.convertValue(it, Extras::class.java)
                    },
                    comment = rantGroup.comment
                )
            }
            
            Result.success(PlaceProfileRes(
                place = Place(
                    id = place.id.toString(),
                    googlePlaceId = place.googlePlaceId,
                    formattedAddress = place.formattedAddress,
                    lat = place.lat,
                    lng = place.lng
                ),
                ratings = PlaceRatings(
                    counts = RatingCounts(
                        landlord = landlordCount,
                        apartment = apartmentCount
                    ),
                    averages = RatingAverages(
                        landlord = landlordAverage,
                        apartment = apartmentAverage,
                        extras = extrasAverages.takeIf { it.isNotEmpty() }
                    ),
                    recent = recentRatings
                )
            ))
            
        } catch (e: Exception) {
            Result.failure(ServiceError.DatabaseError(e))
        }
    }
    
    private fun addToAverage(averages: MutableMap<String, Double>, key: String, value: Double) {
        val current = averages[key] ?: 0.0
        averages[key] = (current + value) / 2.0
    }
}