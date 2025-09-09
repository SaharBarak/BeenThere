package com.beenthere.common

import com.beenthere.user.UserEntity
import java.time.Instant

// User DTOs
data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val dateOfBirth: java.time.LocalDate? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class ProfileDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val dateOfBirth: java.time.LocalDate? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val isVerified: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class SubscriptionDto(
    val status: String,
    val periodEnd: Instant? = null,
    val planId: String? = null
)

// Extension functions
fun UserEntity.toDto(): UserDto = UserDto(
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
