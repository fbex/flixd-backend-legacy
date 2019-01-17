package io.fbex.flixd.backend.watchlist

import classification.IntegrationTest
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.willReturn
import io.fbex.flixd.backend.tmdb.MOVIE_THE_GODFATHER_DETAILS
import io.fbex.flixd.backend.tmdb.TmdbHttpResponseException
import io.fbex.flixd.backend.tmdb.TmdbWebClient
import io.fbex.flixd.backend.vod.VOD_THE_GODFATHER
import io.fbex.flixd.backend.vod.VodFacade
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.web.reactive.server.KotlinBodySpec
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.body
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono

@IntegrationTest
@AutoConfigureWebTestClient
class WatchlistMongoIntegrationTests {

    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var mongo: MongoOperations

    @MockBean
    private lateinit var tmdbWebClient: TmdbWebClient

    @MockBean
    private lateinit var vodFacade: VodFacade

    @Autowired
    private lateinit var watchlistRepository: WatchlistRepository

    @BeforeEach
    fun setUp() {
        populateMongoDb()
    }

    @Test
    fun `test it`() {
        // TODO: replace this test
        val res = watchlistRepository.findByIdAndAggregateMovies(WATCHLIST_USER_2_ITEMS.id!!).block()

        assertThat(res).isEqualTo(AGGREGATED_WATCHLIST_USER_2_ITEMS)
    }

    @Test
    fun `test update`() {
        // TODO: replace this test
        val now = getWatchlistById(WATCHLIST_USER_2_ITEMS.id!!)
        assertThat(now).isEqualTo(WATCHLIST_USER_2_ITEMS)

        val update = watchlistRepository.findByUserIdAndAddToMovieIds("1", MOVIE_PULP_FICTION.id!!).block()

        val then = getWatchlistById(WATCHLIST_USER_2_ITEMS.id!!)
        assertThat(then).isEqualTo(WATCHLIST_USER_2_ITEMS)
    }

    @Test
    fun `test purge movieId`() {
        // TODO: replace this test
        val now = getWatchlistById(WATCHLIST_USER_2_ITEMS.id!!)
        assertThat(now).isEqualTo(WATCHLIST_USER_2_ITEMS)

        val update = watchlistRepository.findByUserIdAndPullFromMovieIds("1", MOVIE_CITIZEN_KANE.id!!).block()

        val then = getWatchlistById(WATCHLIST_USER_2_ITEMS.id!!)
        assertThat(then).isEqualTo(WATCHLIST_USER_2_ITEMS)
    }

    @Test
    fun `get all movies`() {
        val expectedJson = """
            [
                {
                    "id": "5c3b9440f5eb260004cae818",
                    "title": "Pulp Fiction"
                },
                {
                    "id": "5c3b91d0f5eb260004cae817",
                    "title": "GoodFellas"
                },
                {
                    "id": "5c40fe2ddc0a020004a153bc",
                    "title": "Citizen Kane"
                },
                {
                    "id": "5c41037fdc0a020004a153bd",
                    "title": "The Matrix"
                }
            ]
        """.trimIndent()
        client.get()
            .uri(BASE_PATH)
            .accept(APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk
            .expectBody().json(expectedJson)
    }

    @Test
    fun `delete movie by id`() {
        client.delete()
            .uri("$BASE_PATH/${MOVIE_GOODFELLAS.id}")
            .accept(APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_CITIZEN_KANE, MOVIE_THE_MATRIX)
    }

    @Test
    fun `create new movie`() {
        given { tmdbWebClient.getMovieDetails(MOVIE_THE_GODFATHER.tmdbId) }
            .willReturn(Mono.just(MOVIE_THE_GODFATHER_DETAILS))

        given {
            vodFacade.searchTitle(
                tmdbId = MOVIE_THE_GODFATHER_DETAILS.id,
                title = MOVIE_THE_GODFATHER_DETAILS.title,
                releaseDate = MOVIE_THE_GODFATHER_DETAILS.release_date
            )
        } willReturn { Mono.just(VOD_THE_GODFATHER) }

        client.post()
            .uri(BASE_PATH)
            .accept(APPLICATION_JSON_UTF8)
            .body(Mono.just(CreateMovieRequest(tmdbId = MOVIE_THE_GODFATHER.tmdbId)))
            .exchange()
            .expectStatus().isOk
            .expectBody<Movie>().isEqualToIgnoringId(MOVIE_THE_GODFATHER)

        assertThat(getAllMovies()).containsExactlyIgnoringId(
            MOVIE_PULP_FICTION, MOVIE_GOODFELLAS, MOVIE_CITIZEN_KANE, MOVIE_THE_MATRIX, MOVIE_THE_GODFATHER
        )
    }

    @Test
    fun `skip movie creation if it already exists in the db`() {
        client.post()
            .uri(BASE_PATH)
            .accept(APPLICATION_JSON_UTF8)
            .body(Mono.just(CreateMovieRequest(tmdbId = MOVIE_GOODFELLAS.tmdbId)))
            .exchange()
            .expectStatus().isOk
            .expectBody<Movie>().isEqualToIgnoringId(MOVIE_GOODFELLAS)

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS, MOVIE_CITIZEN_KANE, MOVIE_THE_MATRIX)

        verifyNoMoreInteractions(tmdbWebClient, vodFacade)
    }

    @Test
    fun `returns status NOT_FOUND if no VOD information can get collected`() {
        given { tmdbWebClient.getMovieDetails(MOVIE_THE_GODFATHER.tmdbId) }
            .willReturn(Mono.just(MOVIE_THE_GODFATHER_DETAILS))

        given {
            vodFacade.searchTitle(
                tmdbId = MOVIE_THE_GODFATHER_DETAILS.id,
                title = MOVIE_THE_GODFATHER_DETAILS.title,
                releaseDate = MOVIE_THE_GODFATHER_DETAILS.release_date
            )
        } willReturn { Mono.empty() }

        client.post()
            .uri(BASE_PATH)
            .accept(APPLICATION_JSON_UTF8)
            .body(Mono.just(CreateMovieRequest(tmdbId = MOVIE_THE_GODFATHER.tmdbId)))
            .exchange()
            .expectStatus().isNotFound

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS, MOVIE_CITIZEN_KANE, MOVIE_THE_MATRIX)
    }

    @Test
    fun `returns status NOT_FOUND if the provided ID is not found on TMDb`() {
        given { tmdbWebClient.getMovieDetails(MOVIE_THE_GODFATHER.tmdbId) }
            .willReturn(Mono.empty())

        client.post()
            .uri(BASE_PATH)
            .accept(APPLICATION_JSON_UTF8)
            .body(Mono.just(CreateMovieRequest(tmdbId = MOVIE_THE_GODFATHER.tmdbId)))
            .exchange()
            .expectStatus().isNotFound

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS, MOVIE_CITIZEN_KANE, MOVIE_THE_MATRIX)

        verifyNoMoreInteractions(vodFacade)
    }

    @Test
    fun `returns status NOT_FOUND if communicatin with TMDb fails`() {
        given { tmdbWebClient.getMovieDetails(MOVIE_THE_GODFATHER.tmdbId) }
            .willThrow(TmdbHttpResponseException::class.java)

        client.post()
            .uri(BASE_PATH)
            .accept(APPLICATION_JSON_UTF8)
            .body(Mono.just(CreateMovieRequest(tmdbId = MOVIE_THE_GODFATHER.tmdbId)))
            .exchange()
            .expectStatus().is5xxServerError

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS, MOVIE_CITIZEN_KANE, MOVIE_THE_MATRIX)

        verifyNoMoreInteractions(vodFacade)
    }

    private fun populateMongoDb() {
        // reset and create "movies" collection
        mongo.dropCollection(Movie::class.java)
        mongo.createCollection(Movie::class.java)
        listOf(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS, MOVIE_CITIZEN_KANE, MOVIE_THE_MATRIX).forEach {
            mongo.save(it, MOVIES_COLLECTION)
        }

        // reset and create "watchlists" collection
        mongo.dropCollection(Watchlist::class.java)
        mongo.createCollection(Watchlist::class.java)
        listOf(
            WATCHLIST_USER_2_ITEMS, WATCHLIST_USER_NO_ITEMS, WATCHLIST_USER_ALL_ITEMS
        ).forEach {
            mongo.save(it, WATCHLISTS_COLLECTION)
        }
    }

    private fun getAllMovies(): List<Movie> = mongo.findAll(Movie::class.java)
    private fun getWatchlistById(id: String): Watchlist = mongo.findById(id, Watchlist::class.java)!!

    private fun <T> ListAssert<T>.containsExactlyIgnoringId(vararg values: T) =
        this.usingElementComparatorIgnoringFields("id").containsExactly(*values)

    private fun <T> KotlinBodySpec<T>.isEqualToIgnoringId(other: T) = this.consumeWith {
        assertThat(it.responseBody).isEqualToIgnoringGivenFields(other, "id")
    }

    private companion object {
        const val BASE_PATH = "/watchlist"
        const val MOVIES_COLLECTION = "movies"
        const val WATCHLISTS_COLLECTION = "watchlists"
    }
}
