package com.beenthere.api

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<com.beenthere.Application>().with(TestcontainersConfiguration::class).run(*args)
}
