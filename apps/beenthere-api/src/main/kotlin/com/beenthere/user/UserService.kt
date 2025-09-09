package com.beenthere.user

import com.beenthere.auth.PasswordService
import com.beenthere.error.ServiceError
import com.beenthere.error.ServiceErrorFactory
import com.michaelbull.result.Result
import com.michaelbull.result.Ok
import com.michaelbull.result.Err
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
    private val userRepo: UserRepo,
    private val passwordService: PasswordService
) {

    fun createUser(
        email: String,
        password: String,
        intent: String
    ): Mono<Result<UserEntity, ServiceError>> {
        return userRepo.existsByEmail(email)
            .flatMap { exists ->
                if (exists) {
                    Mono.just(ServiceErrorFactory.userAlreadyExists("User with email $email already exists"))
                } else {
                    if (!passwordService.isPasswordValid(password)) {
                        Mono.just(com.michaelbull.result.Err(ServiceError.WeakPassword(
                            "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
                        )))
                    } else {
                        val userId = UUID.randomUUID().toString()
                        val passwordHash = passwordService.encodePassword(password)
                        
                        // Generate names based on intent or use placeholder
                        val firstName = if (intent == "house") "Landlord" else "Roommate"
                        val lastName = "User"
                        
                        val user = UserEntity(
                            id = userId,
                            email = email,
                            passwordHash = passwordHash,
                            firstName = firstName,
                            lastName = lastName
                        )
                        
                        userRepo.save(user)
                            .map { Ok(it) }
                    }
                }
            }
            .onErrorReturn(ServiceErrorFactory.internalError("Failed to create user"))
    }

    fun findById(id: String): Mono<Result<UserEntity, ServiceError>> {
        return userRepo.findActiveById(id)
            .map { user -> Ok(user) }
            .onErrorReturn(ServiceErrorFactory.userNotFound("User with id $id not found"))
    }

    fun findByEmail(email: String): Mono<Result<UserEntity, ServiceError>> {
        return userRepo.findActiveByEmail(email)
            .map { user -> Ok(user) }
            .onErrorReturn(ServiceErrorFactory.userNotFound("User with email $email not found"))
    }

    fun updateUser(id: String, updateRequest: UpdateUserRequest): Mono<Result<UserEntity, ServiceError>> {
        return userRepo.findActiveById(id)
            .flatMap { existingUser ->
                val updatedUser = existingUser.copy(
                    firstName = updateRequest.firstName ?: existingUser.firstName,
                    lastName = updateRequest.lastName ?: existingUser.lastName,
                    phone = updateRequest.phone ?: existingUser.phone,
                    dateOfBirth = updateRequest.dateOfBirth ?: existingUser.dateOfBirth,
                    bio = updateRequest.bio ?: existingUser.bio,
                    profileImageUrl = updateRequest.profileImageUrl ?: existingUser.profileImageUrl,
                    updatedAt = java.time.Instant.now()
                )
                
                userRepo.save(updatedUser)
                    .map { Ok(it) as Result<UserEntity, ServiceError> }
            }
            .onErrorReturn(ServiceErrorFactory.userNotFound("User with id $id not found"))
    }

    fun deactivateUser(id: String): Mono<Result<Unit, ServiceError>> {
        return userRepo.findActiveById(id)
            .flatMap { user ->
                val deactivatedUser = user.copy(
                    isActive = false,
                    updatedAt = java.time.Instant.now()
                )
                userRepo.save(deactivatedUser)
                    .map { Ok(Unit) }
            }
            .onErrorReturn(ServiceErrorFactory.userNotFound("User with id $id not found"))
    }
}

data class UpdateUserRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val dateOfBirth: java.time.LocalDate? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null
)
