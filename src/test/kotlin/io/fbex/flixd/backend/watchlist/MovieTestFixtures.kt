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
