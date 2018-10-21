package io.fbex.flixd.backend.watchlist

import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@Component
class MovieHandler(
    val movieRepository: MovieRepository
) {

    fun all(request: ServerRequest): Mono<ServerResponse> {
        val movies = movieRepository.findAll()
        return ok().contentType(APPLICATION_JSON_UTF8).body(movies, Movie::class.java)
    }

    fun byId(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id")
        val movie = movieRepository.findById(id)
        return ok().contentType(APPLICATION_JSON_UTF8).body(movie, Movie::class.java)
    }
}
