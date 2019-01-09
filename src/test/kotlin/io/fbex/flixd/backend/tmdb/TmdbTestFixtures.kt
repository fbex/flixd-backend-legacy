package io.fbex.flixd.backend.tmdb

import io.fbex.flixd.backend.tmdb.model.Genre
import io.fbex.flixd.backend.tmdb.model.Language
import io.fbex.flixd.backend.tmdb.model.MovieDetails
import io.fbex.flixd.backend.tmdb.model.MovieResult
import io.fbex.flixd.backend.tmdb.model.ProductionCompany
import io.fbex.flixd.backend.tmdb.model.ProductionCountry
import java.time.LocalDate

val MOVIE_PULP_FICTION_SEARCH = MovieResult(
    id = 680,
    vote_count = 13582,
    video = false,
    vote_average = 8.4,
    title = "Pulp Fiction",
    popularity = 29.612,
    poster_path = "/dM2w364MScsjFf8pfMbaWUcWrR.jpg",
    original_title = "Pulp Fiction",
    original_language = "en",
    genre_ids = listOf(53, 80),
    backdrop_path = "/4cDFJr4HnXN5AdPw4AKrmLlMWdO.jpg",
    adult = false,
    overview = "A burger-loving hit man, his philosophical partner, a drug-addled gangster's moll and a washed-up boxer converge in this sprawling, comedic crime caper. Their adventures unfurl in three stories that ingeniously trip back and forth in time.",
    release_date = LocalDate.of(1994, 9, 10)
)

val MOVIE_PULP_FICTION_DETAILS = MovieDetails(
    adult = false,
    backdrop_path = "/4cDFJr4HnXN5AdPw4AKrmLlMWdO.jpg",
    belongs_to_collection = null,
    budget = 8000000,
    genres = listOf(Genre(id = 53, name = "Thriller"), Genre(id = 80, name = "Crime")),
    homepage = null,
    id = 680,
    imdb_id = "tt0110912",
    original_language = "en",
    original_title = "Pulp Fiction",
    overview = "A burger-loving hit man, his philosophical partner, a drug-addled gangster's moll and a washed-up boxer converge in this sprawling, comedic crime caper. Their adventures unfurl in three stories that ingeniously trip back and forth in time.",
    popularity = 35.143,
    poster_path = "/dM2w364MScsjFf8pfMbaWUcWrR.jpg",
    production_companies = listOf(
        ProductionCompany(
            id = 14,
            logo_path = "/m6AHu84oZQxvq7n1rsvMNJIAsMu.png",
            name = "Miramax",
            origin_country = "US"
        ),
        ProductionCompany(
            id = 59,
            logo_path = "/yH7OMeSxhfP0AVM6iT0rsF3F4ZC.png",
            name = "A Band Apart",
            origin_country = "US"
        ),
        ProductionCompany(
            id = 216,
            logo_path = null,
            name = "Jersey Films",
            origin_country = ""
        )
    ),
    production_countries = listOf(ProductionCountry(iso_3166_1 = "US", name = "United States of America")),
    release_date = LocalDate.of(1994, 9, 10),
    revenue = 213928762,
    runtime = 154,
    spoken_languages = listOf(
        Language(
            iso_639_1 = "en",
            name = "English"
        ),
        Language(
            iso_639_1 = "es",
            name = "Español"
        ),
        Language(
            iso_639_1 = "fr",
            name = "Français"
        )
    ),
    status = "Released",
    tagline = "Just because you are a character doesn't mean you have character.",
    title = "Pulp Fiction",
    video = false,
    vote_average = 8.4,
    vote_count = 13606
)
