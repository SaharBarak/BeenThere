package com.beenthere.controllers

import com.beenthere.dto.auth.GoogleAuthReq
import com.beenthere.dto.auth.GoogleAuthRes
import com.beenthere.services.AuthService
import com.beenthere.common.toResponseEntity
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    
    @PostMapping("/google")
    suspend fun authenticateWithGoogle(
        @Valid @RequestBody req: GoogleAuthReq
    ): ResponseEntity<*> {
        val result = authService.authenticateWithGoogle(req)
        return result.toResponseEntity()
    }
}