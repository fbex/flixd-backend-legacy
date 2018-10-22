package io.fbex.flixd.backend.watchlist

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MovieRepository : ReactiveMongoRepository<Movie, String>
