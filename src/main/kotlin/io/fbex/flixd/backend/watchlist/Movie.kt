package io.fbex.flixd.backend.watchlist

import io.fbex.flixd.backend.tmdb.model.Genre
import io.fbex.flixd.backend.vod.model.Offer
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "movies")
data class Movie(
    @Id var id: String? = null, // when id is null, spring data generates an id automatically
    val tmdbId: Int,
    val justWatchId: Int,
    val imdbId: String?,
    val tomatoId: Int?,
    val title: String,
    val originalTitle: String,
    val releaseDate: LocalDate,
    val runtime: Int?,
    val genres: List<Genre>,
    val overview: String?,
    val posterPath: String?,
    val adult: Boolean,
    val scores: Scores,
    val offers: List<Offer>
)

data class Scores(
    val tmdb: Double,
    val imdb: Double? = null,
    val tomatoMeter: Int? = null,
    val tomatoAudience: Int? = null,
    val metacritic: Int? = null
)
