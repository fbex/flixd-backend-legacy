package io.fbex.flixd.backend.vod

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import io.fbex.flixd.backend.vod.model.Provider
import io.fbex.flixd.backend.vod.model.SearchResult
import io.fbex.flixd.backend.vod.model.Stream
import io.fbex.flixd.backend.vod.model.StreamingType
import io.fbex.flixd.backend.vod.model.StreamingUrls
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class JustWatchFacadeTests {

    private val feignClientMock: JustWatchFeignClient = mock()
    private val objectMapper = ObjectMapper()

    private val testee = JustWatchFacade(feignClientMock)

    @Test
    fun `sends request and parses all matching data`() {
        // Mock JustWatch
        val justWatchResponse =
            javaClass.getResourceAsStream("justwatch_search_ps1-pulp_fiction.json").bufferedReader()
        val jsonNode = objectMapper.readTree(justWatchResponse)
        val expectedRequest = JustWatchSearchRequest(
            query = TITLE,
            page_size = 1,
            content_types = listOf("movie")
        )

        given { feignClientMock.search(expectedRequest) }
            .willReturn(jsonNode)

        val result = testee.searchTitle(TITLE)

        assertThat(result).isEqualTo(
            SearchResult(
                id = 112130,
                title = "Pulp Fiction",
                originalTitle = "Pulp Fiction",
                year = 1994,
                tmdbId = 680,
                tomatoId = 13863,
                streams = listOf(
                    Stream(
                        type = StreamingType.FLATRATE,
                        provider = Provider.NETFLIX,
                        urls = StreamingUrls(
                            web = "http://www.netflix.com/title/880640",
                            android = "nflx://www.netflix.com/Browse?q=action%3Dplay%26source%3Dmerchweb%26target_url%3Dhttp%3A%2F%2Fmovi.es%2F7tEu",
                            ios = "nflx://www.netflix.com/Browse?q=action%3Dview_details%26target_url%3Dhttp%253A%252F%252Fmovi.es%252F7tEu"
                        )
                    ),
                    Stream(
                        type = StreamingType.FLATRATE,
                        provider = Provider.AMAZON_PRIME,
                        urls = StreamingUrls(
                            web = "https://www.amazon.de/gp/product/B00IGK7D06?camp=1638&creativeASIN=B00IGK7D06&ie=UTF8&linkCode=xm2&tag=movie0c6-21",
                            android = "intent://watch.amazon.de/watch?asin=DE&contentType=MOVIE&territory=B00IGK7D06&ref_=atv_dp_pb_core#Intent;scheme=https;package=com.amazon.avod.thirdpartyclient;end",
                            ios = "aiv-de://aiv/resume?_encoding=UTF8&asin=B00IGK7D06&time=0"
                        )
                    ),
                    Stream(
                        type = StreamingType.FLATRATE,
                        provider = Provider.SKY_GO,
                        urls = StreamingUrls(
                            web = "https://ad.zanox.com/ppc/?41728307C1348667999&ulp=[[http://www.skygo.sky.de/film/thriller/pulp-fiction/asset/filmsection/122681.html]]",
                            android = null,
                            ios = null
                        )
                    ),
                    Stream(
                        type = StreamingType.FLATRATE,
                        provider = Provider.SKY_TICKET,
                        urls = StreamingUrls(
                            web = "https://ad.zanox.com/ppc/?41728307C1348667999&ulp=[[http://skyticket.sky.de/film/thriller/pulp-fiction/asset/filmsection/122681.html]]",
                            android = null,
                            ios = null
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `returns null if JustWatch returns an empty string`() {
        val jsonNode = "{}".toJsonNode()
        val expectedRequest = JustWatchSearchRequest(
            query = TITLE,
            page_size = 1,
            release_year_from = YEAR,
            release_year_until = YEAR,
            content_types = listOf("movie")
        )

        given { feignClientMock.search(expectedRequest) }
            .willReturn(jsonNode)

        val result = testee.searchTitle(TITLE, YEAR)

        assertThat(result).isNull()
    }

    @Test
    fun `returns the minimal result set if some data is missing in JustWatch response`() {
        val jsonNode = """
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
        val expectedRequest = JustWatchSearchRequest(
            query = TITLE,
            page_size = 1,
            content_types = listOf("movie")
        )

        given { feignClientMock.search(expectedRequest) }
            .willReturn(jsonNode)

        val result = testee.searchTitle(TITLE)

        assertThat(result).isEqualTo(
            SearchResult(
                id = 112130,
                title = "Pulp Fiction",
                originalTitle = "Pulp Fiction",
                year = 1994,
                tmdbId = null,
                tomatoId = null,
                streams = emptyList()
            )
        )
    }

    @Test
    fun `returns null if JustWatch returns an emtpy items list`() {
        val jsonNode = """
            {
                "page": 0,
                "page_size": 1,
                "total_pages": 0,
                "total_results": 0,
                "items": []
            }
        """.trimIndent().toJsonNode()
        val expectedRequest = JustWatchSearchRequest(
            query = TITLE,
            page_size = 1,
            release_year_from = YEAR,
            release_year_until = YEAR,
            content_types = listOf("movie")
        )

        given { feignClientMock.search(expectedRequest) }
            .willReturn(jsonNode)

        val result = testee.searchTitle(TITLE, YEAR)

        assertThat(result).isNull()
    }

    private fun String.toJsonNode() = objectMapper.readTree(this)

    private companion object {
        const val TITLE = "Pulp Fiction"
        const val YEAR = 1994
    }
}
