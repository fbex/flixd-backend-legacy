package io.fbex.flixd.backend.vod

import com.fasterxml.jackson.databind.JsonNode
import io.fbex.flixd.backend.vod.model.Provider
import io.fbex.flixd.backend.vod.model.SearchResult
import io.fbex.flixd.backend.vod.model.Stream
import io.fbex.flixd.backend.vod.model.StreamingType
import io.fbex.flixd.backend.vod.model.StreamingUrls
import org.springframework.stereotype.Service

/**
 * Facade for accessing the JustWatch API.
 * Always use this class instead of the FeignClient directly.
 */
@Service
class JustWatchFacade(
        private val feignClient: JustWatchFeignClient
) {

    /**
     * Queries with a page size of 1 to optimize performance (due to the amount of provided data).
     * If this tends to deliver inaccurate results, increase
     * the page size and filter the result set for a matching result.
     *
     * To get a more accurate result provide the release year of the title.
     *
     * Checking the tmdb:id would also be an option for movies.
     * JustWatch doesn't provide an tmdb:id for tv shows though.
     */
    fun searchTitle(title: String, year: Int? = null): SearchResult? {
        val request = JustWatchSearchRequest(
                query = title,
                page_size = 1,
                release_year_from = year,
                release_year_until = year,
                content_types = listOf("movie") // "show" for tv shows
        )

        val result = feignClient.search(request)
        val items: List<JsonNode>? = result.get("items")?.toList()

        if (items.isNullOrEmpty()) return null

        return items[0].let { resultSet ->
            SearchResult(
                    id = resultSet.get("id").asInt(),
                    title = resultSet.get("title").asText(),
                    originalTitle = resultSet.get("original_title").asText(),
                    year = resultSet.get("original_release_year").asInt(),
                    tmdbId = resultSet.findProviderId("tmdb:id"),
                    tomatoId = resultSet.findProviderId("tomato:id"),
                    streams = resultSet.get("offers")
                            ?.toList()
                            ?.filter { it.get("monetization_type").asText() == StreamingType.FLATRATE.value }
                            ?.distinctBy { it.get("provider_id") }
                            ?.map {
                                Stream(
                                        type = StreamingType.FLATRATE,
                                        provider = Provider.forProviderId(it.get("provider_id").asInt()),
                                        urls = it.get("urls").let { urls ->
                                            StreamingUrls(
                                                    web = urls.get("standard_web")?.asText(),
                                                    android = urls.get("deeplink_android")?.asText(),
                                                    ios = urls.get("deeplink_ios")?.asText()
                                            )
                                        }
                                )
                            } ?: emptyList()
            )
        }
    }

    private fun JsonNode.findProviderId(providerType: String): Int? =
            this.get("scoring")
                    ?.toList()
                    ?.find { it.get("provider_type").asText() == providerType }
                    ?.get("value")
                    ?.asInt()
}
