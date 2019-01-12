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

    @BeforeEach
    fun setUp() {
        populateMongoDb()
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
            .uri("$BASE_PATH/${MOVIE_PULP_FICTION.id}")
            .accept(APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk

        assertThat(getAllMovies()).containsExactly(MOVIE_GOODFELLAS)
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

        assertThat(getAllMovies()).containsExactlyIgnoringId(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS, MOVIE_THE_GODFATHER)
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

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS)

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

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS)
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

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS)

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

        assertThat(getAllMovies()).containsExactly(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS)

        verifyNoMoreInteractions(vodFacade)
    }

    private fun populateMongoDb() {
        mongo.dropCollection(Movie::class.java)
        mongo.createCollection(Movie::class.java)
        listOf(MOVIE_PULP_FICTION, MOVIE_GOODFELLAS).forEach {
            mongo.save(it, MOVIE_COLLECTION)
        }
    }

    private fun getAllMovies(): List<Movie> = mongo.findAll(Movie::class.java)

    private fun <T> ListAssert<T>.containsExactlyIgnoringId(vararg values: T) =
        this.usingElementComparatorIgnoringFields("id").containsExactly(*values)

    private fun <T> KotlinBodySpec<T>.isEqualToIgnoringId(other: T) = this.consumeWith {
        assertThat(it.responseBody).isEqualToIgnoringGivenFields(other, "id")
    }

    private companion object {
        const val BASE_PATH = "/watchlist"
        const val MOVIE_COLLECTION = "movies"
    }
}
