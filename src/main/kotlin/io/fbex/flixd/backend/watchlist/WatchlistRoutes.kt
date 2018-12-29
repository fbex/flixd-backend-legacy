package io.fbex.flixd.backend.watchlist

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.router

@Configuration
class WatchlistRoutes : WebFluxConfigurer {

    @Bean
    fun routeMovie(movieHandler: MovieHandler) = router {
        accept(APPLICATION_JSON_UTF8).nest {
            path("/movies").nest {
                GET("", movieHandler::all)
                GET("/{id}", movieHandler::byId)
                POST("", movieHandler::create)
                PUT("/{id}", movieHandler::update)
                DELETE("/{id}", movieHandler::deleteById)
            }
        }
    }
}
