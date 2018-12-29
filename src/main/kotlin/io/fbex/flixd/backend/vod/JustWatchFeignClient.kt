package io.fbex.flixd.backend.vod

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "justwatch", url = "\${justwatch.base-url}")
interface JustWatchFeignClient {

    @PostMapping(value = ["/titles/de_DE/popular"], consumes = ["application/json"])
    fun search(@RequestBody request: JustWatchSearchRequest): JsonNode
}

data class JustWatchSearchRequest(
    val query: String,
    val page_size: Int = 1,
    val content_types: List<String>? = null,
    val providers: List<String>? = null,
    val monetization_types: List<String>? = null,
    val release_year_from: Int? = null,
    val release_year_until: Int? = null,
    val presentation_types: String? = null,
    val genres: List<String>? = null,
    val languages: List<String>? = null,
    val min_price: String? = null,
    val max_price: String? = null,
    val scoring_filter_types: List<String>? = null,
    val cinema_release: Int? = null
)
