package io.fbex.flixd.backend.tmdb

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.router

@Configuration
class TmdbRoutes : WebFluxConfigurer {

    @Bean
    fun routeTmdb(tmdbHandler: TmdbHandler) = router {
        accept(MediaType.APPLICATION_JSON_UTF8).nest {
            path("/tmdb").nest {
                POST("/search", tmdbHandler::search)
                GET("/movie/{id}", tmdbHandler::getMovieById)
            }
        }
    }
}
