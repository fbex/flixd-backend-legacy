package io.fbex.flixd.backend.vod.model

import io.fbex.flixd.backend.vod.SearchRequestBody

data class SearchResult(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val year: Int,
    val tmdbId: Int?,
    val tomatoId: Int?,
    val streams: List<Stream>
) {

    /**
     * Verify if the search result equals the requested data.
     *
     * If both request and result contain a "tmdbId", detection can be verified safely.
     * Otherwise checks of the "title" and "year" should provide a good enough guess.
     */
    fun matchesQuery(request: SearchRequestBody): Boolean {
        // if request and result contain tmdbIds, a comparison shows a definite match
        if (this.tmdbId != null && request.tmdbId != null) {
            return this.tmdbId == request.tmdbId
        }

        // if tmdb id cannot be compared, the other parameters must be compared
        if (this.title.toUpperCase() != request.title.toUpperCase() &&
            this.originalTitle.toUpperCase() != request.title.toUpperCase()
        ) return false
        if (request.year != null && request.year != this.year) return false
        return true
    }
}
