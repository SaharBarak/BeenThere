package com.beenthere.repositories

import com.beenthere.entities.PlaceEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlaceRepository : CoroutineCrudRepository<PlaceEntity, UUID>, CoroutineSortingRepository<PlaceEntity, UUID> {
    suspend fun findByGooglePlaceId(googlePlaceId: String): PlaceEntity?
    suspend fun findByLatAndLng(lat: Double, lng: Double): PlaceEntity?
    
    // Custom query to find nearby places within a radius (approximate using lat/lng bounds)
    @Query("""
        SELECT * FROM places 
        WHERE lat BETWEEN :lat - :radius AND :lat + :radius
        AND lng BETWEEN :lng - :radius AND :lng + :radius
        LIMIT 1
    """)
    suspend fun findNearbyPlace(lat: Double, lng: Double, radius: Double): PlaceEntity?
}