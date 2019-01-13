package io.fbex.flixd.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
class FlixdBackendApplication

fun main(args: Array<String>) {
    runApplication<FlixdBackendApplication>(*args)
}
