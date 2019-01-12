package io.fbex.flixd.backend.watchlist

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface MovieRepository : ReactiveMongoRepository<Movie, String> {

    fun findByTmdbId(tmdbId: Int): Mono<Movie>
}
