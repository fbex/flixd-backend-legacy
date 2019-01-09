package io.fbex.flixd.backend.tmdb

import io.fbex.flixd.backend.tmdb.model.MovieDetails
import io.fbex.flixd.backend.tmdb.model.SearchResult
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class TmdbWebClient(
    private val tmdbProperties: TmdbProperties
) {

    private val client = WebClient.create(tmdbProperties.baseUrl)

    /**
     * Since flixd only supports movies currently, the search is also limited to these.
     *
     * To also support TV shows, query the "/search/muti" API instead.
     * Responses from that resource will also indicate which media_type the results
     * are (i.e. movie or tv show).
     */
    fun searchMovies(query: String): Mono<SearchResult> = client.get()
        .uri { builder ->
            builder
                .path("/search/movie")
                .queryParam("api_key", tmdbProperties.apiKey)
                .queryParam("language", LANGUAGE)
                .queryParam("page", 1)
                .queryParam("include_adult", false)
                .queryParam("query", query)
                .build()
        }.retrieve().bodyToMono(SearchResult::class.java)

    fun getMovieDetails(tmdbId: Int): Mono<MovieDetails> = client.get()
        .uri { builder ->
            builder
                .path("/movie/$tmdbId")
                .queryParam("api_key", tmdbProperties.apiKey)
                .queryParam("language", LANGUAGE)
                .build()
        }
        .retrieve()
        .onStatus({ it == HttpStatus.NOT_FOUND }) { Mono.empty() }
        .onStatus(HttpStatus::isError) { throw TmdbHttpErrorException(statusCode = it.rawStatusCode()) }
        .bodyToMono(MovieDetails::class.java)

    private companion object {
        const val LANGUAGE = "en-US"
    }
}

class TmdbHttpErrorException(statusCode: Int) : IllegalStateException("TMDb responded with http status [$statusCode]")
