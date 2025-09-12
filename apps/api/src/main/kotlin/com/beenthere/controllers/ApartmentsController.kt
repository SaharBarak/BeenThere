package com.beenthere.controllers

import com.beenthere.dto.apartments.*
import com.beenthere.services.ApartmentsService
import com.beenthere.common.toResponseEntity
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/apartments")
class ApartmentsController(
    private val apartmentsService: ApartmentsService
) {
    
    @PostMapping
    suspend fun createListing(
        @Valid @RequestBody req: CreateListingReq,
        @RequestHeader("X-User-ID") ownerUserId: String
    ): ResponseEntity<*> {
        val result = apartmentsService.createListing(req, UUID.fromString(ownerUserId))
        return result.toResponseEntity(HttpStatus.CREATED)
    }
    
    @GetMapping("/feed")
    suspend fun getApartmentsFeed(
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "50") limit: Int,
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) minPrice: Int?,
        @RequestParam(required = false) maxPrice: Int?,
        @RequestParam(required = false) rooms: Int?,
        @RequestParam(required = false) furnished: Boolean?,
        @RequestParam(required = false) pets: Boolean?,
        @RequestParam(required = false) smoking: Boolean?
    ): ResponseEntity<*> {
        val filters = mutableMapOf<String, String>()
        city?.let { filters["city"] = it }
        minPrice?.let { filters["minPrice"] = it.toString() }
        maxPrice?.let { filters["maxPrice"] = it.toString() }
        rooms?.let { filters["rooms"] = it.toString() }
        furnished?.let { filters["furnished"] = it.toString() }
        pets?.let { filters["pets"] = it.toString() }
        smoking?.let { filters["smoking"] = it.toString() }
        
        val result = apartmentsService.getApartmentsFeed(
            cursor = cursor,
            limit = limit,
            filters = filters
        )
        return result.toResponseEntity()
    }
}

@RestController
@RequestMapping("/api/v1")
class ListingSwipesController(
    private val apartmentsService: ApartmentsService
) {
    
    @PostMapping("/listing-swipes")
    suspend fun swipeOnListing(
        @Valid @RequestBody req: ListingSwipeReq,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<*> {
        val result = apartmentsService.swipeOnListing(req, UUID.fromString(userId))
        return result.toResponseEntity(HttpStatus.CREATED)
    }
}