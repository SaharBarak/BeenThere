package com.beenthere.listing

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ListingRepo : ReactiveCrudRepository<ListingEntity, String> {
    
    @Query("SELECT * FROM listings WHERE landlord_id = :landlordId AND is_active = true ORDER BY created_at DESC")
    fun findByLandlordId(landlordId: String): Flux<ListingEntity>
    
    @Query("SELECT * FROM listings WHERE is_active = true ORDER BY created_at DESC")
    fun findAllActive(): Flux<ListingEntity>
    
    @Query("SELECT * FROM listings WHERE id = :id AND is_active = true")
    fun findActiveById(id: String): Mono<ListingEntity>
    
    @Query("""
        SELECT * FROM listings 
        WHERE is_active = true 
        AND city = :city 
        AND state = :state
        ORDER BY created_at DESC
    """)
    fun findByLocation(city: String, state: String): Flux<ListingEntity>
    
    @Query("""
        SELECT * FROM listings 
        WHERE is_active = true 
        AND rent_amount BETWEEN :minRent AND :maxRent
        ORDER BY created_at DESC
    """)
    fun findByRentRange(minRent: java.math.BigDecimal, maxRent: java.math.BigDecimal): Flux<ListingEntity>
    
    @Query("""
        SELECT * FROM listings 
        WHERE is_active = true 
        AND bedrooms = :bedrooms
        ORDER BY created_at DESC
    """)
    fun findByBedrooms(bedrooms: Int): Flux<ListingEntity>
    
    @Query("""
        SELECT * FROM listings 
        WHERE is_active = true 
        AND property_type = :propertyType
        ORDER BY created_at DESC
    """)
    fun findByPropertyType(propertyType: PropertyType): Flux<ListingEntity>
}
