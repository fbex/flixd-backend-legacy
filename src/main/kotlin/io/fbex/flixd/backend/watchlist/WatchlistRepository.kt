package io.fbex.flixd.backend.watchlist

import com.mongodb.client.result.UpdateResult
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.LookupOperation.newLookup
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Repository
interface WatchlistRepository : ReactiveMongoRepository<Watchlist, String>, WatchlistRepositoryCustom

interface WatchlistRepositoryCustom {

    fun findByUserIdAndAddToMovieIds(userId: String, movieId: String): Mono<UpdateResult>
    fun findByUserIdAndPullFromMovieIds(userId: String, movieId: String): Mono<UpdateResult>
    fun findByIdAndAggregateMovies(id: String): Mono<AggregatedWatchlist>
}

class WatchlistRepositoryCustomImpl(
    private val template: ReactiveMongoTemplate
) : WatchlistRepositoryCustom {

    override fun findByUserIdAndAddToMovieIds(userId: String, movieId: String): Mono<UpdateResult> {
        val query = query(where("userId").isEqualTo(userId))
        val update = Update().addToSet("movieIds", ObjectId(movieId))
        return template.updateFirst(query, update, Watchlist::class.java)
    }

    override fun findByUserIdAndPullFromMovieIds(userId: String, movieId: String): Mono<UpdateResult> {
        val query = query(where("userId").isEqualTo(userId))
        val update = Update().pull("movieIds", ObjectId(movieId))
        return template.updateFirst(query, update, Watchlist::class.java)
    }

    override fun findByIdAndAggregateMovies(id: String): Mono<AggregatedWatchlist> {
        val lookup = newLookup()
            .from("movies")
            .localField("movieIds")
            .foreignField("_id")
            .`as`("movies")
        val aggregation = newAggregation(match(where("_id").`is`(id)), lookup)
        return template
            .aggregateAndReturn(AggregatedWatchlist::class.java)
            .inCollection("watchlists")
            .by(aggregation)
            .all().toMono()
    }
}
