package com.beenthere.controllers

import com.beenthere.dto.rant.CreateRantCombinedReq
import com.beenthere.dto.rant.CreateRoommateRantReq
import com.beenthere.services.RantService
import com.beenthere.common.toResponseEntity
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/rant")
class RantController(
    private val rantService: RantService
) {
    
    @PostMapping
    suspend fun createCombinedRant(
        @Valid @RequestBody req: CreateRantCombinedReq,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<*> {
        val result = rantService.createCombinedRant(req, UUID.fromString(userId))
        return result.toResponseEntity(HttpStatus.CREATED)
    }
    
    @PostMapping("/roommate")
    suspend fun createRoommateRant(
        @Valid @RequestBody req: CreateRoommateRantReq,
        @RequestHeader("X-User-ID") userId: String
    ): ResponseEntity<*> {
        val result = rantService.createRoommateRant(req, UUID.fromString(userId))
        return result.toResponseEntity(HttpStatus.CREATED)
    }
}