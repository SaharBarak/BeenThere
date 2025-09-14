package com.beenthere.config

import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import javax.sql.DataSource

/**
 * Flyway configuration for database migrations
 * Runs BEFORE R2DBC repositories are initialized to ensure tables exist
 */
@Configuration
@Profile("!test")
@Order(0) // Run early in startup sequence
class FlywayConfig {
    
    @Value("\${spring.datasource.url}")
    private lateinit var jdbcUrl: String
    
    @Value("\${spring.datasource.username}")
    private lateinit var username: String
    
    @Value("\${spring.datasource.password}")
    private lateinit var password: String
    
    @Bean("flywayDataSource")
    fun flywayDataSource(): DataSource {
        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = jdbcUrl
        dataSource.username = username
        dataSource.password = password
        dataSource.driverClassName = "org.postgresql.Driver"
        dataSource.maximumPoolSize = 2 // Small pool for migrations only
        return dataSource
    }
    
    @Bean(initMethod = "migrate")
    fun flyway(flywayDataSource: DataSource): Flyway {
        return Flyway.configure()
            .dataSource(flywayDataSource)
            .locations("classpath:db/migration")
            .schemas("public")
            .table("flyway_schema_history")
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .load()
    }
}