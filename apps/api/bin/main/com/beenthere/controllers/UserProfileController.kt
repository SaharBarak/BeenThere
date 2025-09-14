package com.beenthere.controllers

import com.beenthere.dto.users.UserProfile
import com.beenthere.services.UserProfileService
import com.beenthere.common.toResponseEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/users")
class UserProfileController(
    private val userProfileService: UserProfileService
) {
    
    @GetMapping("/{id}/profile")
    suspend fun getUserProfile(
        @PathVariable id: String
    ): ResponseEntity<*> {
        val result = userProfileService.getUserProfile(UUID.fromString(id))
        return result.toResponseEntity()
    }
}