package com.beenthere.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

/**
 * Security configuration for BeenThere API.
 * MVP: Permit all requests for development.
 * TODO: Add JWT authentication in subsequent PRs.
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    
    /**
     * Development security: permit all requests.
     * Used for MVP development and testing.
     */
    @Bean
    @Profile("dev", "mock")
    fun devSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges.anyExchange().permitAll()
            }
            .csrf { csrf -> csrf.disable() }
            .build()
    }
    
    /**
     * Production security: JWT authentication required.
     * TODO: Implement proper JWT validation.
     */
    @Bean
    @Profile("prod")
    fun prodSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }
            .csrf { csrf -> csrf.disable() }
            .build()
    }
}