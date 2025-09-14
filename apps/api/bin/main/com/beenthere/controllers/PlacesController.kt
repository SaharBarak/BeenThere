package com.beenthere.controllers

import com.beenthere.dto.places.PlaceSnapReq
import com.beenthere.dto.places.PlaceProfileRes
import com.beenthere.services.PlaceService
import com.beenthere.common.toResponseEntity
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/places")
class PlacesController(
    private val placeService: PlaceService
) {
    
    @PostMapping("/snap")
    suspend fun snapPlace(
        @Valid @RequestBody req: PlaceSnapReq
    ): ResponseEntity<*> {
        val result = placeService.snapPlace(req)
        return result.toResponseEntity(HttpStatus.CREATED)
    }
    
    @GetMapping("/{placeId}")
    suspend fun getPlaceProfile(
        @PathVariable placeId: String
    ): ResponseEntity<*> {
        val result = placeService.getPlaceProfile(UUID.fromString(placeId))
        return result.toResponseEntity()
    }
}