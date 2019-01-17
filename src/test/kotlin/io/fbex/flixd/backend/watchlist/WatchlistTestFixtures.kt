package io.fbex.flixd.backend.watchlist

import org.bson.types.ObjectId

val WATCHLIST_USER_2_ITEMS = Watchlist(
    id = "5c3fa022175b1965b01a4f10",
    userId = "1",
    movieIds = setOf(ObjectId(MOVIE_GOODFELLAS.id), ObjectId(MOVIE_CITIZEN_KANE.id))
)

val WATCHLIST_USER_NO_ITEMS = Watchlist(
    id = "5c41015b13bb3b5038e2171f",
    userId = "2",
    movieIds = emptySet()
)

val WATCHLIST_USER_ALL_ITEMS = Watchlist(
    id = "5c41017113bb3b5038e21720",
    userId = "3",
    movieIds = setOf(
        ObjectId(MOVIE_THE_MATRIX.id), ObjectId(MOVIE_PULP_FICTION.id),
        ObjectId(MOVIE_CITIZEN_KANE.id), ObjectId(MOVIE_GOODFELLAS.id)
    )
)

val AGGREGATED_WATCHLIST_USER_2_ITEMS = AggregatedWatchlist(
    id = "5c3fa022175b1965b01a4f10",
    userId = "1",
    movies = listOf(MOVIE_GOODFELLAS, MOVIE_CITIZEN_KANE)
)

val AGGREGATED_WATCHLIST_USER_NO_ITEMS = AggregatedWatchlist(
    id = "5c41015b13bb3b5038e2171f",
    userId = "2",
    movies = emptyList()
)

val AGGREGATED_WATCHLIST_USER_ALL_ITEMS = AggregatedWatchlist(
    id = "5c41017113bb3b5038e21720",
    userId = "3",
    movies = listOf(MOVIE_THE_MATRIX, MOVIE_PULP_FICTION, MOVIE_CITIZEN_KANE, MOVIE_GOODFELLAS)
)
