package io.fbex.flixd.backend.vod

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.router

@Configuration
class VodRoutes : WebFluxConfigurer {

    @Bean
    fun routeVod(vodHandler: VodHandler) = router {
        accept(MediaType.APPLICATION_JSON_UTF8).nest {
            path("/vod").nest {
                POST("/search", vodHandler::searchByTitleAndYear)
            }
        }
    }
}
