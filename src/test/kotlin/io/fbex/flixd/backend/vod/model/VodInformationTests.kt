package io.fbex.flixd.backend.vod.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class VodInformationTests {

    private val testee = VodInformation(
        justWatchId = 1,
        title = TITLE,
        originalTitle = ORIGINAL_TITLE,
        year = YEAR,
        tmdbId = TMDB_ID,
        tomatoId = 789,
        offers = emptyList()
    )

    private val testeeWithoutTmdbId = VodInformation(
        justWatchId = 1,
        title = TITLE,
        originalTitle = ORIGINAL_TITLE,
        year = YEAR,
        tmdbId = null,
        tomatoId = 789,
        offers = emptyList()
    )

    @Test
    fun `returns true if the tmdbId matches`() {
        val matchesQuery = testee.matchesQuery(
            tmdbId = TMDB_ID,
            title = "",
            year = 0)
        assertThat(matchesQuery).isTrue()
    }

    @Test
    fun `returns false if the tmdbId doesn't match`() {
        val matchesQuery = testee.matchesQuery(
            tmdbId = 815,
            title = "",
            year = 0
        )

        assertThat(matchesQuery).isFalse()
    }

    @Test
    fun `returns true if title matches`() {
        val matchesQuery = testeeWithoutTmdbId.matchesQuery(
            tmdbId = 0,
            title = TITLE,
            year = YEAR
        )

        assertThat(matchesQuery).isTrue()
    }

    @Test
    fun `returns true if original title matches`() {
        val matchesQuery = testeeWithoutTmdbId.matchesQuery(
            tmdbId = 0,
            title = ORIGINAL_TITLE,
            year = YEAR
        )

        assertThat(matchesQuery).isTrue()
    }

    @Test
    fun `returns false if title and original title doesn't match`() {
        val matchesQuery = testeeWithoutTmdbId.matchesQuery(
            tmdbId = 0,
            title = "murks",
            year = 0
        )

        assertThat(matchesQuery).isFalse()
    }

    @Test
    fun `returns false if year doesn't match`() {
        val matchesQuery = testeeWithoutTmdbId.matchesQuery(
            tmdbId = 0,
            title = TITLE,
            year = 2019
        )

        assertThat(matchesQuery).isFalse()
    }

    @Test
    fun `returns true if title and year matches`() {
        val matchesQuery = testeeWithoutTmdbId.matchesQuery(
            tmdbId = 0,
            title = TITLE,
            year = YEAR
        )

        assertThat(matchesQuery).isTrue()
    }

    @Test
    fun `returns true if everything matches`() {
        val matchesQuery = testee.matchesQuery(
            tmdbId = TMDB_ID,
            title = TITLE,
            year = YEAR
        )

        assertThat(matchesQuery).isTrue()
    }

    @Test
    fun `returns true if titles have different cases`() {
        val matchesQuery = testeeWithoutTmdbId.matchesQuery(
            tmdbId = 0,
            title = TITLE_CASE,
            year = YEAR
        )

        assertThat(matchesQuery).isTrue()
    }

    @Test
    fun `returns true if original title has different case`() {
        val matchesQuery = testeeWithoutTmdbId.matchesQuery(
            tmdbId = 0,
            title = ORIGINAL_TITLE_CASE,
            year = YEAR
        )

        assertThat(matchesQuery).isTrue()
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
