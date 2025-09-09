package com.beenthere.auth

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.ServiceError
import com.beenthere.error.toApiResponse
import com.beenthere.user.UserEntity
import com.beenthere.user.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userService: UserService,
    private val passwordService: PasswordService,
    private val jwtService: JwtService,
) {
    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody request: SignupRequest,
    ): Mono<ResponseEntity<ApiResponse<SignupResponse>>> {
        return userService.createUser(request.email, request.password, request.intent)
            .map { result ->
                result.toApiResponse().toResponseEntity()
            }
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
    ): Mono<ResponseEntity<ApiResponse<LoginResponse>>> {
        return userService.findByEmail(request.email)
            .map { userResult ->
                userResult.toApiResponse().toResponseEntity()
            }
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestHeader("Authorization") authHeader: String,
    ): Mono<ResponseEntity<ApiResponse<LoginResponse>>> {
        val token = authHeader.removePrefix("Bearer ")
        return try {
            val userId = jwtService.validateRefreshToken(token)
            userService.findById(userId)
                .map { userResult ->
                    userResult.toApiResponse<LoginResponse>().toResponseEntity()
                }
        } catch (e: Exception) {
            Mono.just(ApiResponse.error<LoginResponse>("Invalid refresh token: ${e.message}").toResponseEntity())
        }
    }
}

data class SignupRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    val password: String,
    @field:NotBlank(message = "Intent is required")
    val intent: String, // "house" or "roommates"
)

data class LoginRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String,
)

data class SignupResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto,
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val createdAt: java.time.Instant,
)
