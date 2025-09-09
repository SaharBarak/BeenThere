package com.beenthere.auth

import com.beenthere.common.ApiResponse
import com.beenthere.common.toResponseEntity
import com.beenthere.error.ServiceError
import com.beenthere.error.ServiceErrorFactory
import com.beenthere.error.toApiResponse
import com.beenthere.user.UserService
import com.michaelbull.result.Result
import com.michaelbull.result.Ok
import com.michaelbull.result.Err
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val userService: UserService,
    private val passwordService: PasswordService,
    private val jwtService: JwtService
) {

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody request: SignupRequest): Mono<ResponseEntity<ApiResponse<SignupResponse>>> {
        return userService.createUser(request.email, request.password, request.intent)
            .map { result ->
                result.fold(
                    success = { user ->
                        val tokens = jwtService.generateTokens(user.id, user.email)
                        val response = SignupResponse(
                            accessToken = tokens.accessToken,
                            refreshToken = tokens.refreshToken,
                            user = UserDto(
                                id = user.id,
                                email = user.email,
                                firstName = user.firstName,
                                lastName = user.lastName,
                                createdAt = user.createdAt
                            )
                        )
                        ApiResponse.success(response).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<SignupResponse>().toResponseEntity()
                    }
                )
            }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): Mono<ResponseEntity<ApiResponse<LoginResponse>>> {
        return userService.findByEmail(request.email)
            .map { userResult ->
                userResult.fold(
                    success = { user ->
                        if (passwordService.matches(request.password, user.passwordHash)) {
                            val tokens = jwtService.generateTokens(user.id, user.email)
                            val response = LoginResponse(
                                accessToken = tokens.accessToken,
                                refreshToken = tokens.refreshToken
                            )
                            ApiResponse.success(response).toResponseEntity()
                        } else {
                            ServiceErrorFactory.invalidCredentials().toApiResponse<LoginResponse>().toResponseEntity()
                        }
                    },
                    failure = { error ->
                        error.toApiResponse<LoginResponse>().toResponseEntity()
                    }
                )
            }
    }

    @PostMapping("/refresh")
    fun refresh(@RequestHeader("Authorization") authHeader: String): Mono<ResponseEntity<ApiResponse<LoginResponse>>> {
        val token = authHeader.removePrefix("Bearer ")
        return jwtService.validateRefreshToken(token)
            .fold(
                success = { userId ->
                    userService.findById(userId)
                        .map { userResult ->
                            userResult.fold(
                                success = { user ->
                                    val tokens = jwtService.generateTokens(user.id, user.email)
                                    val response = LoginResponse(
                                        accessToken = tokens.accessToken,
                                        refreshToken = tokens.refreshToken
                                    )
                                    ApiResponse.success(response).toResponseEntity()
                                },
                                failure = { error ->
                                    error.toApiResponse<LoginResponse>().toResponseEntity()
                                }
                            )
                        }
                },
                failure = { error ->
                    Mono.just(error.toApiResponse<LoginResponse>().toResponseEntity())
                }
            )
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
    val intent: String // "house" or "roommates"
)

data class LoginRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class SignupResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val createdAt: java.time.Instant
)
