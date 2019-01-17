package io.fbex.flixd.backend.watchlist

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "watchlists")
data class Watchlist(
    @Id val id: String? = null,
    val userId: String,
    val movieIds: Set<ObjectId>
)

data class AggregatedWatchlist(
    val id: String,
    val userId: String,
    val movies: List<Movie>
)
