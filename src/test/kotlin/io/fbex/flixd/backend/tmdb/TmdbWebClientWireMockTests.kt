package io.fbex.flixd.backend.tmdb

import classification.IntegrationTest
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@IntegrationTest
internal class TmdbWebClientWireMockTests {

    @Autowired
    private lateinit var testee: TmdbWebClient

    private lateinit var wireMock: WireMockServer

    @BeforeEach
    fun setUp() {
        wireMock = WireMockServer(8091)
        wireMock.start()
    }

    @AfterEach
    fun tearDown() {
        wireMock.stop()
    }

    @Test
    fun `search movie`() {
        val response = javaClass.getResourceAsStream("tmdb_movie_search-pulp_fiction.json").bufferedReader().readText()

        wireMock.givenThat(
            get("/search/movie?api_key=123456789&language=en-US&page=1&include_adult=false&query=pulp%20fiction")
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(response)
                )
        )

        val result = testee.searchMovies("pulp fiction").block()

        with(result!!) {
            assertThat(page).isEqualTo(1)
            assertThat(total_pages).isEqualTo(1)
            assertThat(total_results).isEqualTo(4)
            assertThat(results).hasSize(4)
            assertThat(results[0]).isEqualTo(MOVIE_PULP_FICTION_SEARCH)
        }
    }

    @Test
    fun `get movie details`() {
        val response = javaClass.getResourceAsStream("tmdb_movie_details-pulp_fiction.json").bufferedReader().readText()

        wireMock.givenThat(
            get(movieUrlForId(PULP_FICTION_ID))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(response)
                )
        )

        val result = testee.getMovieDetails(PULP_FICTION_ID).block()

        assertThat(result).isEqualTo(MOVIE_PULP_FICTION_DETAILS)
    }

    @Test
    fun `return null for status NOT_FOUND`() {
        wireMock.givenThat(
            get(movieUrlForId(PULP_FICTION_ID))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                )
        )

        val result = testee.getMovieDetails(PULP_FICTION_ID).block()

        assertThat(result).isNull()
    }

    @Test
    fun `throws for 4xx error`() {
        wireMock.givenThat(
            get(movieUrlForId(PULP_FICTION_ID))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                )
        )

        assertThatThrownBy { testee.getMovieDetails(PULP_FICTION_ID).block() }
            .isInstanceOf(TmdbHttpErrorException::class.java)
            .hasMessage("TMDb responded with http status [400]")
    }

    @Test
    fun `throws for 5xx error`() {
        wireMock.givenThat(
            get(movieUrlForId(PULP_FICTION_ID))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                )
        )

        assertThatThrownBy { testee.getMovieDetails(PULP_FICTION_ID).block() }
            .isInstanceOf(TmdbHttpErrorException::class.java)
            .hasMessage("TMDb responded with http status [500]")
    }

    private fun movieUrlForId(id: Int) = "/movie/$id?api_key=123456789&language=en-US"

    private companion object {
        const val PULP_FICTION_ID = 680
    }
}
