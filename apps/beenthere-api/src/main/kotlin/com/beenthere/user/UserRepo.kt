package com.beenthere.user

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepo : ReactiveCrudRepository<UserEntity, String> {
    fun findByEmail(email: String): Mono<UserEntity>

    @Query("SELECT * FROM users WHERE email = :email AND is_active = true")
    fun findActiveByEmail(email: String): Mono<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id AND is_active = true")
    fun findActiveById(id: String): Mono<UserEntity>

    fun existsByEmail(email: String): Mono<Boolean>
}
