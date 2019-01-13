package io.fbex.flixd.backend.vod

import com.fasterxml.jackson.databind.JsonNode
import io.fbex.flixd.backend.vod.model.Offer
import io.fbex.flixd.backend.vod.model.OfferType
import io.fbex.flixd.backend.vod.model.OfferUrls
import io.fbex.flixd.backend.vod.model.Provider
import io.fbex.flixd.backend.vod.model.VodInformation
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

/**
 * A Facade abstracting from the actual JustWatch backend.
 * Always use this instead of the [JustWatchWebClient] directly.
 */
@Service
class VodFacade(
    private val webClient: JustWatchWebClient
) {

    /**
     * Returns [VodInformation] for a requested movie.
     * Performs a check, whether the result matches the requested movie.
     */
    fun searchTitle(tmdbId: Int, title: String, releaseDate: LocalDate): Mono<VodInformation> {
        val request = JustWatchSearchRequest(
            query = title,
            release_year_until = releaseDate.year,
            release_year_from = releaseDate.year,
            content_types = listOf(JustWatchContentType.MOVIE)
        )
        return webClient.search(request).flatMap { searchResult ->
            val items = searchResult.get("items")?.toList() ?: emptyList()
            if (items.isEmpty()) return@flatMap Mono.empty<VodInformation>()

            val result = mapResult(items)
            if (result.matchesQuery(tmdbId = tmdbId, title = title, year = releaseDate.year)) {
                Mono.just(result)
            } else {
                Mono.empty<VodInformation>()
            }
        }
    }

    private fun mapResult(items: List<JsonNode>) = items.first().let { resultSet ->
        VodInformation(
            justWatchId = resultSet.get("id").asInt(),
            title = resultSet.get("title").asText(),
            originalTitle = resultSet.get("original_title").asText(),
            year = resultSet.get("original_release_year").asInt(),
            tmdbId = resultSet.findProviderId("tmdb:id"),
            tomatoId = resultSet.findProviderId("tomato:id"),
            offers = resultSet.get("offers")
                ?.toList()
                ?.filter { it.get("monetization_type").asText() == OfferType.FLATRATE.value }
                ?.distinctBy { it.get("provider_id") }
                ?.map {
                    Offer(
                        type = OfferType.FLATRATE,
                        provider = Provider.forProviderId(it.get("provider_id").asInt()),
                        urls = it.get("urls").let { urls ->
                            OfferUrls(
                                web = urls.get("standard_web")?.asText(),
                                android = urls.get("deeplink_android")?.asText(),
                                ios = urls.get("deeplink_ios")?.asText()
                            )
                        }
                    )
                } ?: emptyList()
        )
    }

    private fun JsonNode.findProviderId(providerType: String): Int? =
        this.get("scoring")
            ?.toList()
            ?.find { it.get("provider_type").asText() == providerType }
            ?.get("value")
            ?.asInt()
}
