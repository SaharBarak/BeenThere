package com.beenthere.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("users")
data class UserEntity(
    @Id
    val id: String,
    
    @Column("email")
    val email: String,
    
    @Column("password_hash")
    val passwordHash: String,
    
    @Column("first_name")
    val firstName: String,
    
    @Column("last_name")
    val lastName: String,
    
    @Column("phone")
    val phone: String? = null,
    
    @Column("date_of_birth")
    val dateOfBirth: java.time.LocalDate? = null,
    
    @Column("bio")
    val bio: String? = null,
    
    @Column("profile_image_url")
    val profileImageUrl: String? = null,
    
    @Column("is_verified")
    val isVerified: Boolean = false,
    
    @Column("is_active")
    val isActive: Boolean = true,
    
    @Column("created_at")
    val createdAt: Instant = Instant.now(),
    
    @Column("updated_at")
    val updatedAt: Instant = Instant.now()
)
