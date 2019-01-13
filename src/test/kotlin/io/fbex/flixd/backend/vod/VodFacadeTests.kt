package io.fbex.flixd.backend.vod

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.fbex.flixd.backend.vod.model.VodInformation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import java.time.LocalDate

internal class VodFacadeTests {

    private val webClientMock: JustWatchWebClient = mock()
    private val objectMapper = ObjectMapper()

    private val testee = VodFacade(webClientMock)

    @Test
    fun `parses all matching data`() {
        // given
        val justWatchResponse = resourceAsJsonNode("justwatch_search_ps1-pulp_fiction.json")
        given { webClientMock.search(JUSTWATCH_SEARCH_REQUEST) }
            .willReturn(Mono.just(justWatchResponse))

        // when
        val result = testee.searchTitle(TMDB_ID, TITLE, RELEASE_DATE).block()

        // then
        assertThat(result).isEqualTo(VOD_PULP_FICTION)
    }

    @Test
    fun `returns null if JustWatch returns an empty string`() {
        // given
        val justWatchResponse = "{}".toJsonNode()
        given { webClientMock.search(JUSTWATCH_SEARCH_REQUEST) }
            .willReturn(Mono.just(justWatchResponse))

        // when
        val result = testee.searchTitle(TMDB_ID, TITLE, RELEASE_DATE).block()

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `returns the minimal result set if some data is missing in JustWatch response`() {
        val justWatchResponse = """
            {
              "page": 0,
              "page_size": 1,
              "total_pages": 1,
              "total_results": 1,
              "items": [
                {
                  "id": 112130,
                  "title": "Pulp Fiction",
                  "original_release_year": 1994,
                  "original_title": "Pulp Fiction"
                }
              ]
            }
        """.trimIndent().toJsonNode()
        given { webClientMock.search(JUSTWATCH_SEARCH_REQUEST) }
            .willReturn(Mono.just(justWatchResponse))

        // when
        val result = testee.searchTitle(TMDB_ID, TITLE, RELEASE_DATE).block()

        // then
        assertThat(result).isEqualTo(
            VodInformation(
                justWatchId = 112130,
                title = "Pulp Fiction",
                originalTitle = "Pulp Fiction",
                year = 1994,
                tmdbId = null,
                tomatoId = null,
                offers = emptyList()
            )
        )
    }

    @Test
    fun `returns null if JustWatch returns an emtpy items list`() {
        val justWatchResponse = """
            {
                "page": 0,
                "page_size": 1,
                "total_pages": 0,
                "total_results": 0,
                "items": []
            }
        """.trimIndent().toJsonNode()
        given { webClientMock.search(JUSTWATCH_SEARCH_REQUEST) }
            .willReturn(Mono.just(justWatchResponse))

        // when
        val result = testee.searchTitle(TMDB_ID, TITLE, RELEASE_DATE).block()

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `returns null if the response data doesn't match with the requested parameters`() {
        val justWatchResponse = """
            {
              "page": 0,
              "page_size": 1,
              "total_pages": 1,
              "total_results": 1,
              "items": [
                {
                  "id": 20164,
                  "title": "The Room",
                  "original_release_year": 2003,
                  "original_title": "The Room"
                }
              ]
            }
        """.trimIndent().toJsonNode()
        given { webClientMock.search(JUSTWATCH_SEARCH_REQUEST) }
            .willReturn(Mono.just(justWatchResponse))

        // when
        val result = testee.searchTitle(TMDB_ID, TITLE, RELEASE_DATE).block()

        // then
        assertThat(result).isNull()
    }

    private fun resourceAsJsonNode(resource: String): JsonNode {
        val string = javaClass.getResourceAsStream(resource).bufferedReader()
        return objectMapper.readTree(string)
    }

    private fun String.toJsonNode() = objectMapper.readTree(this)

    private companion object {
        const val TMDB_ID = 680
        const val TITLE = "Pulp Fiction"
        val RELEASE_DATE: LocalDate = LocalDate.of(1994, 9, 10)

        val JUSTWATCH_SEARCH_REQUEST = JustWatchSearchRequest(
            query = TITLE,
            release_year_from = RELEASE_DATE.year,
            release_year_until = RELEASE_DATE.year,
            content_types = listOf(JustWatchContentType.MOVIE),
            page_size = 1
        )
    }
}
