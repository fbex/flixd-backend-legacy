package io.fbex.flixd.backend.watchlist

import io.fbex.flixd.backend.tmdb.model.Genre
import io.fbex.flixd.backend.vod.model.Offer
import io.fbex.flixd.backend.vod.model.OfferType
import io.fbex.flixd.backend.vod.model.OfferUrls
import io.fbex.flixd.backend.vod.model.Provider
import java.time.LocalDate

val MOVIE_PULP_FICTION = Movie(
    id = "5c3b9440f5eb260004cae818",
    tmdbId = 680,
    justWatchId = 112130,
    imdbId = "tt0110912",
    tomatoId = 13863,
    title = "Pulp Fiction",
    originalTitle = "Pulp Fiction",
    releaseDate = LocalDate.of(1994, 9, 10),
    runtime = 154,
    genres = listOf(Genre(id = 53, name = "Thriller"), Genre(id = 80, name = "Crime")),
    overview = "A burger-loving hit man, his philosophical partner, a drug-addled gangster's moll and a washed-up boxer converge in this sprawling, comedic crime caper. Their adventures unfurl in three stories that ingeniously trip back and forth in time.",
    posterPath = "/dM2w364MScsjFf8pfMbaWUcWrR.jpg",
    adult = false,
    scores = Scores(tmdb = 8.4),
    offers = listOf(
        Offer(
            type = OfferType.FLATRATE,
            provider = Provider.NETFLIX,
            urls = OfferUrls(
                web = "http://www.netflix.com/title/880640",
                android = "nflx://www.netflix.com/Browse?q=action%3Dplay%26source%3Dmerchweb%26target_url%3Dhttp%3A%2F%2Fmovi.es%2F7tEu",
                ios = "nflx://www.netflix.com/Browse?q=action%3Dview_details%26target_url%3Dhttp%253A%252F%252Fmovi.es%252F7tEu"
            )
        ),
        Offer(
            type = OfferType.FLATRATE,
            provider = Provider.AMAZON_PRIME,
            urls = OfferUrls(
                web = "https://www.amazon.de/gp/product/B00IGK7D06?camp=1638&creativeASIN=B00IGK7D06&ie=UTF8&linkCode=xm2&tag=movie0c6-21",
                android = "intent://watch.amazon.de/watch?asin=DE&contentType=MOVIE&territory=B00IGK7D06&ref_=atv_dp_pb_core#Intent;scheme=https;package=com.amazon.avod.thirdpartyclient;end",
                ios = "aiv-de://aiv/resume?_encoding=UTF8&asin=B00IGK7D06&time=0"
            )
        ),
        Offer(
            type = OfferType.FLATRATE,
            provider = Provider.SKY_GO,
            urls = OfferUrls(
                web = "https://ad.zanox.com/ppc/?41728307C1348667999&ulp=[[http://www.skygo.sky.de/film/thriller/pulp-fiction/asset/filmsection/122681.html]]",
                android = null,
                ios = null
            )
        ),
        Offer(
            type = OfferType.FLATRATE,
            provider = Provider.SKY_TICKET,
            urls = OfferUrls(
                web = "https://ad.zanox.com/ppc/?41728307C1348667999&ulp=[[http://skyticket.sky.de/film/thriller/pulp-fiction/asset/filmsection/122681.html]]",
                android = null,
                ios = null
            )
        )
    )
)

val MOVIE_GOODFELLAS = Movie(
    id = "5c3b91d0f5eb260004cae817",
    tmdbId = 769,
    justWatchId = 155787,
    imdbId = "tt0099685",
    tomatoId = null,
    title = "GoodFellas",
    originalTitle = "GoodFellas",
    releaseDate = LocalDate.of(1990, 9, 12),
    runtime = 145,
    genres = listOf(Genre(id = 18, name = "Drama"), Genre(id = 80, name = "Crime")),
    overview = "The true story of Henry Hill, a half-Irish, half-Sicilian Brooklyn kid who is adopted by neighbourhood gangsters at an early age and climbs the ranks of a Mafia family under the guidance of Jimmy Conway.",
    posterPath = "/hAPeXBdGDGmXRPj4OZZ0poH65Iu.jpg",
    adult = false,
    scores = Scores(tmdb = 8.4),
    offers = emptyList()
)

val MOVIE_THE_GODFATHER = Movie(
    id = "5c3b9b99f5eb260004cae819",
    tmdbId = 238,
    justWatchId = 155580,
    imdbId = "tt0068646",
    tomatoId = 12911,
    title = "The Godfather",
    originalTitle = "The Godfather",
    releaseDate = LocalDate.of(1972, 3, 14),
    runtime = 175,
    genres = listOf(Genre(id = 18, name = "Drama"), Genre(id = 80, name = "Crime")),
    overview = "Spanning the years 1945 to 1955, a chronicle of the fictional Italian-American Corleone crime family. When organized crime family patriarch, Vito Corleone barely survives an attempt on his life, his youngest son, Michael steps in to take care of the would-be killers, launching a campaign of bloody revenge.",
    posterPath = "/d4KNaTrltq6bpkFS01pYtyXa09m.jpg",
    adult = false,
    scores = Scores(tmdb = 8.6),
    offers = listOf(
        Offer(
            type = OfferType.FLATRATE,
            provider = Provider.AMAZON_PRIME,
            urls = OfferUrls(
                web = "https://www.amazon.de/gp/product/B00FYV67P2?camp=1638&creativeASIN=B00FYV67P2&ie=UTF8&linkCode=xm2&tag=movie0c6-21",
                android = "intent://watch.amazon.de/watch?asin=DE&contentType=MOVIE&territory=B00FYV67P2&ref_=atv_dp_pb_core#Intent;scheme=https;package=com.amazon.avod.thirdpartyclient;end",
                ios = "aiv-de://aiv/resume?_encoding=UTF8&asin=B00FYV67P2&time=0"
            )
        )
    )
)

val MOVIE_CITIZEN_KANE = Movie(
    id = "5c40fe2ddc0a020004a153bc",
    tmdbId = 15,
    justWatchId = 83648,
    imdbId = "tt0068646",
    tomatoId = 10074,
    title = "Citizen Kane",
    originalTitle = "Citizen Kane",
    releaseDate = LocalDate.of(1941, 4, 30),
    runtime = 119,
    genres = listOf(Genre(id = 9648, name = "Mystery"), Genre(id = 18, name = "Drama")),
    overview = "Newspaper magnate, Charles Foster Kane is taken from his mother as a boy and made the ward of a rich industrialist. As a result, every well-meaning, tyrannical or self-destructive move he makes for the rest of his life appears in some way to be a reaction to that deeply wounding event.",
    posterPath = "/sav0jxhqiH0bPr2vZFU0Kjt2nZL.jpg",
    adult = false,
    scores = Scores(tmdb = 8.1),
    offers = listOf()
)

val MOVIE_THE_MATRIX = Movie(
    id = "5c41037fdc0a020004a153bd",
    tmdbId = 603,
    justWatchId = 10,
    imdbId = "tt0133093",
    tomatoId = 12897,
    title = "The Matrix",
    originalTitle = "The Matrix",
    releaseDate = LocalDate.of(1999, 3, 30),
    runtime = 136,
    genres = listOf(Genre(id = 28, name = "Action"), Genre(id = 878, name = "Science Fiction")),
    overview = "Set in the 22nd century, The Matrix tells the story of a computer hacker who joins a group of underground insurgents fighting the vast and powerful computers who now rule the earth.",
    posterPath = "/hEpWvX6Bp79eLxY1kX5ZZJcme5U.jpg",
    adult = false,
    scores = Scores(tmdb = 8.1),
    offers = listOf()
)
