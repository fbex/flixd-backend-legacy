package io.fbex.flixd.backend.watchlist

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "movies")
data class Movie(
    @Id var id: String? = null, // when id is null, spring data generates an id automatically
    val title: String
)
