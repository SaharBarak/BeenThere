package com.beenthere.user

import com.beenthere.common.ApiResponse
import com.beenthere.common.UserDto
import com.beenthere.common.toResponseEntity
import com.beenthere.user.toDto
import com.beenthere.error.ServiceError
import com.beenthere.error.toApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import jakarta.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun getCurrentUser(@AuthenticationPrincipal userId: String): Mono<ResponseEntity<ApiResponse<UserDto>>> {
        return userService.findById(userId)
            .map { result ->
                result.fold(
                    success = { user ->
                        ApiResponse.success(user.toDto()).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<UserDto>().toResponseEntity()
                    }
                )
            }
    }

    @PutMapping("/me")
    fun updateCurrentUser(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: UpdateUserRequest
    ): Mono<ResponseEntity<ApiResponse<UserDto>>> {
        return userService.updateUser(userId, request)
            .map { result ->
                result.fold(
                    success = { user ->
                        ApiResponse.success(user.toDto()).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<UserDto>().toResponseEntity()
                    }
                )
            }
    }

    @DeleteMapping("/me")
    fun deactivateCurrentUser(@AuthenticationPrincipal userId: String): Mono<ResponseEntity<ApiResponse<Unit>>> {
        return userService.deactivateUser(userId)
            .map { result ->
                result.fold(
                    success = { 
                        ApiResponse.success(Unit).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<Unit>().toResponseEntity()
                    }
                )
            }
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: String): Mono<ResponseEntity<ApiResponse<UserDto>>> {
        return userService.findById(id)
            .map { result ->
                result.fold(
                    success = { user ->
                        ApiResponse.success(user.toDto()).toResponseEntity()
                    },
                    failure = { error ->
                        error.toApiResponse<UserDto>().toResponseEntity()
                    }
                )
            }
    }
}

