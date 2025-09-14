package com.beenthere

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
class ApiApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<ApiApplication>(*args)
}
