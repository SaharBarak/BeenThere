package com.beenthere.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

/**
 * R2DBC Configuration for reactive database access.
 * Enables coroutine repositories and configures connection factory.
 */
@Configuration
@EnableR2dbcRepositories(basePackages = ["com.beenthere"])
class R2dbcConfig(
    private val connectionFactory: ConnectionFactory
) : AbstractR2dbcConfiguration() {
    
    override fun connectionFactory(): ConnectionFactory = connectionFactory
}