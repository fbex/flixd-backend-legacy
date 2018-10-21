package io.fbex.flixd.backend.watchlist

import org.reactivestreams.Publisher
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MovieRepository : ReactiveCrudRepository<Movie, String> {

    private val movies = hashMapOf(
        "1" to Movie(id = "1", title = "Pulp Fiction", year = 1994),
        "2" to Movie(id = "2", title = "Goodfellas", year = 1990),
        "3" to Movie(id = "3", title = "Casino", year = 1995)
    )

    override fun <S : Movie?> save(entity: S): Mono<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAll(): Flux<Movie> = Flux.fromIterable(movies.values)

    override fun deleteById(id: String): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteById(id: Publisher<String>): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(entities: MutableIterable<Movie>): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(entityStream: Publisher<out Movie>): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteAll(): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : Movie?> saveAll(entities: MutableIterable<S>): Flux<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <S : Movie?> saveAll(entityStream: Publisher<S>): Flux<S> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun count(): Mono<Long> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllById(ids: MutableIterable<String>): Flux<Movie> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findAllById(idStream: Publisher<String>): Flux<Movie> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: String): Mono<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun existsById(id: Publisher<String>): Mono<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findById(id: String): Mono<Movie> = Mono.justOrEmpty(movies[id])

    override fun findById(id: Publisher<String>): Mono<Movie> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(entity: Movie): Mono<Void> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
