package com.beenthere.repositories

import com.beenthere.entities.PlaceEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PlaceRepository : CoroutineCrudRepository<PlaceEntity, UUID> {
    
    /**
     * Find place by Google Place ID.
     * Used for place snapping and deduplication.
     */
    suspend fun findByGooglePlaceId(googlePlaceId: String): PlaceEntity?
    
    /**
     * Find place by coordinates with tolerance.
     * Used for matching places when Google Place ID is not available.
     */
    @Query("""
        SELECT * FROM places 
        WHERE ABS(lat - :lat) < 0.0001 AND ABS(lng - :lng) < 0.0001
        ORDER BY (ABS(lat - :lat) + ABS(lng - :lng)) ASC
        LIMIT 1
    """)
    suspend fun findByCoordinatesWithTolerance(lat: Double, lng: Double): PlaceEntity?
}