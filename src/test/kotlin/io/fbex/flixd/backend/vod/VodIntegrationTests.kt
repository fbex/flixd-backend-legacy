package io.fbex.flixd.backend.vod

import classification.IntegrationTest
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.body
import reactor.core.publisher.Mono

@IntegrationTest
@AutoConfigureWebTestClient
class VodIntegrationTests {

    @Autowired
    private lateinit var client: WebTestClient

    private lateinit var wireMock: WireMockServer

    @BeforeEach
    fun setUp() {
        wireMock = WireMockServer(8090)
        wireMock.start()

        val justWatchResponse =
            javaClass.getResourceAsStream("justwatch_search_ps1-pulp_fiction.json").bufferedReader().readText()

        wireMock.givenThat(
            WireMock.post("/titles/de_DE/popular")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody(justWatchResponse)
                )
        )
    }

    @AfterEach
    fun tearDown() {
        wireMock.stop()
    }

    @Test
    fun `search by title returns the correct result`() {
        val expectedJson = """
            {
                "justWatchId": 112130,
                "streams": [
                    {
                        "type": "FLATRATE",
                        "provider": "NETFLIX",
                        "urls": {
                            "web": "http://www.netflix.com/title/880640",
                            "android": "nflx://www.netflix.com/Browse?q=action%3Dplay%26source%3Dmerchweb%26target_url%3Dhttp%3A%2F%2Fmovi.es%2F7tEu",
                            "ios": "nflx://www.netflix.com/Browse?q=action%3Dview_details%26target_url%3Dhttp%253A%252F%252Fmovi.es%252F7tEu"
                        }
                    },
                    {
                        "type": "FLATRATE",
                        "provider": "AMAZON_PRIME",
                        "urls": {
                            "web": "https://www.amazon.de/gp/product/B00IGK7D06?camp=1638&creativeASIN=B00IGK7D06&ie=UTF8&linkCode=xm2&tag=movie0c6-21",
                            "android": "intent://watch.amazon.de/watch?asin=DE&contentType=MOVIE&territory=B00IGK7D06&ref_=atv_dp_pb_core#Intent;scheme=https;package=com.amazon.avod.thirdpartyclient;end",
                            "ios": "aiv-de://aiv/resume?_encoding=UTF8&asin=B00IGK7D06&time=0"
                        }
                    },
                    {
                        "type": "FLATRATE",
                        "provider": "SKY_GO",
                        "urls": {
                            "web": "https://ad.zanox.com/ppc/?41728307C1348667999&ulp=[[http://www.skygo.sky.de/film/thriller/pulp-fiction/asset/filmsection/122681.html]]",
                            "android": null,
                            "ios": null
                        }
                    },
                    {
                        "type": "FLATRATE",
                        "provider": "SKY_TICKET",
                        "urls": {
                            "web": "https://ad.zanox.com/ppc/?41728307C1348667999&ulp=[[http://skyticket.sky.de/film/thriller/pulp-fiction/asset/filmsection/122681.html]]",
                            "android": null,
                            "ios": null
                        }
                    }
                ]
            }
        """.trimIndent()

        client.post()
            .uri("/vod/search")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(CORRECT_REQUEST))
            .exchange()
            .expectStatus().isOk
            .expectBody().json(expectedJson)
    }

    @Test
    fun `returns NOT_FOUND if the JustWatch response doesn't match the request`() {
        val request = SearchRequestBody(
            title = "murks",
            year = 2019,
            tmdbId = null
        )

        client.post()
            .uri("/vod/search")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(request))
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `returns NOT_FOUND if the JustWatch response is empty`() {
        wireMock.givenThat(
            WireMock.post("/titles/de_DE/popular")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .withBody("{}")
                )
        )

        client.post()
            .uri("/vod/search")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .body(Mono.just(CORRECT_REQUEST))
            .exchange()
            .expectStatus().isNotFound
    }

    private companion object {
        val CORRECT_REQUEST = SearchRequestBody(
            title = "Pulp Fiction",
            year = 1994,
            tmdbId = null
        )
    }
}
