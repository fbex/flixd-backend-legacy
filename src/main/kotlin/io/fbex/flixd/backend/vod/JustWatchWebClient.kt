package io.fbex.flixd.backend.vod

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * WebClient handling communication with JustWatch.
 */
@Component
class JustWatchWebClient(
    justWatchProperties: JustWatchProperties
) {

    private val client = WebClient.create(justWatchProperties.baseUrl)

    fun search(request: JustWatchSearchRequest): Mono<JsonNode> = client.post()
        .uri("/titles/de_DE/popular")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .body(fromObject(request))
        .retrieve()
        .onStatus(HttpStatus::isError) { throw JustWatchHttpResponseException(httpStatus = it.statusCode()) }
        .bodyToMono(JsonNode::class.java)
}

/**
 * Request for finding a movie or a TV-show on JustWatch.
 *
 * It contains:
 *  - a query string, which should be the requested title
 *  - the release year of the title
 *  - a list of content types ("movie", "show")
 *  - the requested page size (defaults to 1)
 *
 *
 *  Additional request options, that are not needed with the current
 *  implementation are:
 *
 *  providers:              List<String>?
 *  monetization_types:     List<String>?
 *  presentation_types:     String?
 *  genres:                 List<String>?
 *  languages:              List<String>?
 *  min_price:              String?
 *  max_price:              String?
 *  scoring_filter_types:   List<String>?
 *  cinema_release:         Int
 */
data class JustWatchSearchRequest(
    val query: String,
    val release_year_from: Int? = null,
    val release_year_until: Int? = null,
    val content_types: List<JustWatchContentType>? = null,
    val page_size: Int = 1
)

enum class JustWatchContentType(@JsonValue val value: String) {
    MOVIE("movie"), TV_SHOW("show")
}

class JustWatchHttpResponseException(httpStatus: HttpStatus) :
    IllegalStateException("JustWatch responded with http status [$httpStatus]")
