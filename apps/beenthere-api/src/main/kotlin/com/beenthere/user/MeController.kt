package com.beenthere.user

import com.beenthere.common.ApiResponse
import com.beenthere.common.UserDto
import com.beenthere.common.ProfileDto
import com.beenthere.common.SubscriptionDto
import com.beenthere.common.toResponseEntity
import com.beenthere.error.ServiceError
import com.beenthere.error.toApiResponse
import com.beenthere.subscription.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1")
class MeController(
    private val userService: UserService,
    private val subscriptionService: SubscriptionService
) {

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal userId: String): Mono<ResponseEntity<ApiResponse<MeResponse>>> {
        return userService.findById(userId)
            .flatMap { userResult ->
                userResult.fold(
                    success = { user ->
                        subscriptionService.getSubscriptionStatus(userId)
                            .map { subscriptionResult ->
                                subscriptionResult.fold(
                                    success = { subscription ->
                                        val response = MeResponse(
                                            user = user.toDto(),
                                            profile = user.toProfileDto(),
                                            subscription = subscription
                                        )
                                        ApiResponse.success(response).toResponseEntity()
                                    },
                                    failure = { error ->
                                        error.toApiResponse<MeResponse>().toResponseEntity()
                                    }
                                )
                            }
                    },
                    failure = { error ->
                        Mono.just(error.toApiResponse<MeResponse>().toResponseEntity())
                    }
                )
            }
    }
}

data class MeResponse(
    val user: UserDto,
    val profile: ProfileDto,
    val subscription: SubscriptionDto
)


fun UserEntity.toDto(): UserDto = UserDto(
    id = this.id,
    email = this.email,
    firstName = this.firstName,
    lastName = this.lastName,
    createdAt = this.createdAt
)

fun UserEntity.toProfileDto(): ProfileDto = ProfileDto(
    id = this.id,
    email = this.email,
    firstName = this.firstName,
    lastName = this.lastName,
    phone = this.phone,
    dateOfBirth = this.dateOfBirth,
    bio = this.bio,
    profileImageUrl = this.profileImageUrl,
    isVerified = this.isVerified,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
