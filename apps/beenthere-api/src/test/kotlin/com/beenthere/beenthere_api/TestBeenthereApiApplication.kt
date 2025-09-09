package com.beenthere.beenthere_api

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<BeenthereApiApplication>().with(TestcontainersConfiguration::class).run(*args)
}
