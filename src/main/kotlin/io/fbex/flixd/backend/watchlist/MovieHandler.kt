package io.fbex.flixd.backend.watchlist

import io.fbex.flixd.backend.tmdb.TmdbWebClient
import io.fbex.flixd.backend.vod.VodFacade
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.switchIfEmpty

@Component
class MovieHandler(
    private val movieRepository: MovieRepository,
    private val watchlistRepository: WatchlistRepository,
    private val tmdbWebClient: TmdbWebClient,
    private val vodFacade: VodFacade
) {

    fun all(request: ServerRequest): Mono<ServerResponse> {
        val movies = movieRepository.findAll()
        return ok().contentType(APPLICATION_JSON_UTF8).body(movies, Movie::class.java)
    }

    fun deleteById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable("id")
        return movieRepository.deleteById(id).flatMap {
            ok().build()
        }
    }

    fun create(request: ServerRequest): Mono<ServerResponse> =
            /*
                TODO: adapt the workflow below: therefore add a WatchlistRepository
                Extract tmdbId from request body. Then there are 3 possibilities:

                1. Check if a movie with that tmdbId already exists
                2. If movie exists -> add it's ID to watchlist repository

                1. Get movie details from tmdb
                2. Get streams from JustWatch
                3. Aggregate movie details, streams and imdb/tomato scores
                4. Add movie to MovieRepository
                5. Add movie to WatchlistRepository

                Error scenarios:
                - tmdb doesn't find a result -> 404
                - tmdb error -> 500
                - justwatch doesn't find anything
                    -> Production state: skip and set null/emptyList for the expected data -> log this occurence -> manually inspect
                    -> For testing period: Throw an error, so you notice something went wrong -> inspect it / file a bug
                - justwatch error -> 500
                - repository connection error -> 500
             */
        request.bodyToMono(CreateMovieRequest::class.java).flatMap { createRequest ->
            movieRepository.findByTmdbId(createRequest.tmdbId).switchIfEmpty {
                createMovie(createRequest.tmdbId).flatMap { createdMovie ->
                    movieRepository.save(createdMovie)
                }
            }.flatMap { movie ->
                // add to watchlist, if it doesn't already exist
                watchlistRepository.findByUserIdAndPullFromMovieIds("1", movie.id!!).flatMap {
                    if (it.wasAcknowledged()) {
                        // movie added
                        ok().contentType(APPLICATION_JSON_UTF8).body(fromObject(movie))
                    } else {
                        // movie already existed
                        notFound().build() // TODO: change response status
                    }
                }
            }.switchIfEmpty {
                notFound().build()
            }
        }

    private fun createMovie(tmdbId: Int): Mono<Movie> =
        tmdbWebClient.getMovieDetails(tmdbId).flatMap { movieDetails ->
            vodFacade.searchTitle(
                tmdbId = movieDetails.id,
                title = movieDetails.title,
                releaseDate = movieDetails.release_date
            ).map { vodInformation ->
                Movie(
                    id = null,  // set by the repository
                    tmdbId = movieDetails.id,
                    imdbId = movieDetails.imdb_id,
                    tomatoId = vodInformation.tomatoId,
                    justWatchId = vodInformation.justWatchId,
                    title = movieDetails.title,
                    originalTitle = movieDetails.original_title,
                    releaseDate = movieDetails.release_date,
                    runtime = movieDetails.runtime,
                    genres = movieDetails.genres,
                    overview = movieDetails.overview,
                    posterPath = movieDetails.poster_path,
                    adult = false,
                    scores = Scores(tmdb = movieDetails.vote_average),
                    offers = vodInformation.offers
                )
            }
        }
}

data class CreateMovieRequest(
    val tmdbId: Int
)
