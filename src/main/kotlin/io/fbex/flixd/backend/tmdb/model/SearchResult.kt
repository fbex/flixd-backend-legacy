package io.fbex.flixd.backend.tmdb.model

import java.time.LocalDate

data class SearchResult(
    val page: Int,
    val total_results: Int,
    val total_pages: Int,
    val results: List<MovieResult>
)

data class MovieResult(
    val id: Int,
    val title: String,
    val vote_average: Double,
    val vote_count: Int,
    val video: Boolean,
    val popularity: Double,
    val poster_path: String?,
    val original_language: String,
    val original_title: String,
    val genre_ids: List<Int>,
    val backdrop_path: String?,
    val adult: Boolean,
    val overview: String,
    val release_date: LocalDate?
)
