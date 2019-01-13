package io.fbex.flixd.backend.vod

import classification.IntegrationTest
import com.fasterxml.jackson.databind.JsonNode
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
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
internal class JustWatchWebClientWireMockTests {

    @Autowired
    private lateinit var testee: JustWatchWebClient

    private lateinit var wireMock: WireMockServer

    @BeforeEach
    fun setUp() {
        wireMock = WireMockServer(8090)
        wireMock.start()
    }

    @AfterEach
    fun tearDown() {
        wireMock.stop()
    }

    @Test
    fun `search title returns a JsonNode`() {
        val justWatchResponse =
            javaClass.getResourceAsStream("justwatch_search_ps1-pulp_fiction.json").bufferedReader().readText()

        val requestBody = """
            {
                "query": "pulp fiction",
                "release_year_from": 1994,
                "release_year_until": 1994,
                "content_types": ["movie"],
                "page_size": 1
            }
        """.trimIndent()

        wireMock.givenThat(
            WireMock.post("/titles/de_DE/popular")
                .withRequestBody(equalToJson(requestBody))
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(justWatchResponse)
                )
        )

        val request = JustWatchSearchRequest(
            query = "pulp fiction",
            release_year_from = 1994,
            release_year_until = 1994,
            content_types = listOf(JustWatchContentType.MOVIE)
        )

        val result = testee.search(request).block()

        assertThat(result).isInstanceOf(JsonNode::class.java)
        assertThat(result?.toString()).isEqualToIgnoringWhitespace(justWatchResponse)
    }

    @Test
    fun `handles JustWatch response errors`() {
        wireMock.givenThat(
            WireMock.post("/titles/de_DE/popular")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                )
        )

        val request = JustWatchSearchRequest(
            query = "pulp fiction",
            release_year_from = 1994,
            release_year_until = 1994,
            content_types = listOf(JustWatchContentType.MOVIE)
        )

        assertThatThrownBy { testee.search(request).block() }
            .isInstanceOf(JustWatchHttpResponseException::class.java)
            .hasMessage("JustWatch responded with http status [500 INTERNAL_SERVER_ERROR]")
    }
}
