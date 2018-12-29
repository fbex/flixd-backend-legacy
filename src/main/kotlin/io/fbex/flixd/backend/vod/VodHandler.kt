package io.fbex.flixd.backend.vod

import io.fbex.flixd.backend.vod.model.Stream
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Component
class VodHandler(
    private val justWatchFacade: JustWatchFacade
) {

    fun searchByTitleAndYear(request: ServerRequest): Mono<ServerResponse> =
        request.bodyToMono(SearchRequestBody::class.java).flatMap { searchRequest ->
            val searchResult = justWatchFacade.searchTitle(searchRequest.title, searchRequest.year)

            when {
                searchResult == null -> notFound().build()
                !searchResult.matchesQuery(searchRequest) -> notFound().build()
                else -> {
                    val response = SearchResponse(
                        justWatchId = searchResult.id,
                        streams = searchResult.streams
                    ).toMono()
                    ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(response, SearchResponse::class.java)
                }
            }
        }

    fun searchByFlixdId(flixdId: String) {
        TODO("Lookup id from tmdb. This provides a much cleaner API.")
    }
}

data class SearchRequestBody(
    val title: String,
    val year: Int?,
    val tmdbId: Int?
)

data class SearchResponse(
    val justWatchId: Int,
    val streams: List<Stream>
)
