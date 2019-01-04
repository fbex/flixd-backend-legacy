package io.fbex.flixd.backend.tmdb

import io.fbex.flixd.backend.tmdb.model.SearchResult
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono

@Component
class TmdbHandler(
    private val tmdbWebClient: TmdbWebClient
) {

    fun search(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono(SearchRequest::class.java).flatMap { searchRequest ->
            val result = tmdbWebClient.searchMovies(searchRequest.query)
            ok().contentType(APPLICATION_JSON_UTF8).body(result, SearchResult::class.java)
        }

    fun getMovieById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id").toInt()
        return tmdbWebClient.getMovieDetails(id).flatMap { movie ->
            ok().contentType(APPLICATION_JSON_UTF8).body(fromObject(movie))
        }.switchIfEmpty(notFound().build())
    }
}

data class SearchRequest(
    val query: String
)
