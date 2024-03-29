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
            path("/watchlist").nest {
                GET("", movieHandler::all)
                POST("", movieHandler::create)
                DELETE("/{id}", movieHandler::deleteById)
            }
        }
    }
}
