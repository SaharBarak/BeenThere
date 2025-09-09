package com.beenthere.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
                       .authorizeExchange { exchanges ->
                           exchanges
                               // Public endpoints
                               .pathMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                               .pathMatchers(HttpMethod.POST, "/api/v1/billing/webhook").permitAll()
                               .pathMatchers("/health", "/actuator/**").permitAll()
                               .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                               // All other endpoints require authentication
                               .anyExchange().authenticated()
                       }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .build()
    }
}
