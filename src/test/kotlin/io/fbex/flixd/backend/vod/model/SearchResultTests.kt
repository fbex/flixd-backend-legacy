package io.fbex.flixd.backend.vod.model

import io.fbex.flixd.backend.vod.SearchRequestBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SearchResultTests {

    private val testee = SearchResult(
        id = 1,
        title = TITLE,
        originalTitle = ORIGINAL_TITLE,
        year = YEAR,
        tmdbId = TMDB_ID,
        tomatoId = 789,
        streams = emptyList()
    )

    @Test
    fun `returns true if the tmdbId matches`() {
        val request = SearchRequestBody(
            title = "",
            year = null,
            tmdbId = TMDB_ID
        )

        assertThat(testee.matchesQuery(request)).isTrue()
    }

    @Test
    fun `returns false if the tmdbId doesn't match`() {
        val request = SearchRequestBody(
            title = "",
            year = null,
            tmdbId = 815
        )

        assertThat(testee.matchesQuery(request)).isFalse()
    }

    @Test
    fun `returns true if title matches`() {
        val request = SearchRequestBody(
            title = TITLE,
            year = null,
            tmdbId = null
        )

        assertThat(testee.matchesQuery(request)).isTrue()
    }

    @Test
    fun `returns true if original title matches`() {
        val request = SearchRequestBody(
            title = ORIGINAL_TITLE,
            year = null,
            tmdbId = null
        )

        assertThat(testee.matchesQuery(request)).isTrue()
    }

    @Test
    fun `returns false if title doesn't match`() {
        val request = SearchRequestBody(
            title = "murks",
            year = null,
            tmdbId = null
        )

        assertThat(testee.matchesQuery(request)).isFalse()
    }

    @Test
    fun `returns false if year doesn't match`() {
        val request = SearchRequestBody(
            title = TITLE,
            year = 2019,
            tmdbId = null
        )

        assertThat(testee.matchesQuery(request)).isFalse()
    }

    @Test
    fun `returns true if title and year matches`() {
        val request = SearchRequestBody(
            title = TITLE,
            year = YEAR,
            tmdbId = null
        )

        assertThat(testee.matchesQuery(request)).isTrue()
    }

    @Test
    fun `returns true if everything matches`() {
        val request = SearchRequestBody(
            title = TITLE,
            year = YEAR,
            tmdbId = TMDB_ID
        )

        assertThat(testee.matchesQuery(request)).isTrue()
    }

    @Test
    fun `returns true if titles have different cases`() {
        val request = SearchRequestBody(
            title = TITLE_CASE,
            year = null,
            tmdbId = null
        )

        assertThat(testee.matchesQuery(request)).isTrue()
    }

    @Test
    fun `returns true if original title has different case`() {
        val request = SearchRequestBody(
            title = ORIGINAL_TITLE_CASE,
            year = null,
            tmdbId = null
        )

        assertThat(testee.matchesQuery(request)).isTrue()
    }

    private companion object {
        const val TITLE = "Pulp Fiction"
        const val TITLE_CASE = "Pulp FICTION"
        const val ORIGINAL_TITLE = "Pulp Fiction Original"
        const val ORIGINAL_TITLE_CASE = "Pulp FICTION Original"
        const val YEAR = 1994
        const val TMDB_ID = 1337
    }
}
