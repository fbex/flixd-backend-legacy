package io.fbex.flixd.backend.watchlist

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.findById
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.body
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("integration-test")
class FlixdBackendApplicationTests {

    @Autowired
    private lateinit var client: WebTestClient

    @Autowired
    private lateinit var mongo: MongoOperations

    @Before
    fun setUp() {
        populateMongoDb()
    }

    @Test
    fun `get all movies`() {
        val expectedJson = """
            [
                {
                    "id": "1",
                    "title": "Pulp Fiction"
                },
                {
                    "id": "2",
                    "title": "Goodfellas"
                }
            ]
        """.trimIndent()
        client.get()
                .uri("/movies")
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectBody().json(expectedJson)
    }

    @Test
    fun `get single movie by id`() {
        val expectedJson = """
            {
                "id": "2",
                "title": "Goodfellas"
            }
        """.trimIndent()
        client.get()
                .uri("/movies/2")
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectBody().json(expectedJson)
    }

    @Test
    fun `create new movie`() {
        client.post()
                .uri("/movies")
                .accept(APPLICATION_JSON_UTF8)
                .body(Mono.just(TEST_MOVIE_3_NOID))
                .exchange()
                .expectStatus().isOk
                .expectBody().jsonPath("$.title").isEqualTo("The Godfather")

        assertThat(countAllMovies()).isEqualTo(3)
    }

    @Test
    fun `update movie successfully`() {
        client.put()
                .uri("/movies/1")
                .accept(APPLICATION_JSON_UTF8)
                .body(Mono.just(TEST_MOVIE_1_UPDATE))
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.title").isEqualTo("Matrix")

        assertThat(countAllMovies()).isEqualTo(2)
        assertThat(mongo.findById<Movie>("1")).isEqualTo(TEST_MOVIE_1_UPDATE)
    }

    @Test
    fun `update movie non-matching id error`() {
        client.put()
                .uri("/movies/2")
                .accept(APPLICATION_JSON_UTF8)
                .body(Mono.just(TEST_MOVIE_1_UPDATE))
                .exchange()
                .expectStatus().isBadRequest

        assertThat(countAllMovies()).isEqualTo(2)
        assertThat(mongo.findById<Movie>("1")).isEqualTo(TEST_MOVIE_1)
        assertThat(mongo.findById<Movie>("2")).isEqualTo(TEST_MOVIE_2)
    }

    @Test
    fun `delete movie by id`() {
        client.delete()
                .uri("/movies/1")
                .accept(APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk

        assertThat(countAllMovies()).isEqualTo(1)
    }

    private fun populateMongoDb() {
        mongo.dropCollection(Movie::class.java)
        mongo.createCollection(Movie::class.java)
        listOf(TEST_MOVIE_1, TEST_MOVIE_2).forEach {
            mongo.save(it, MOVIE_COLLECTION)
        }
    }

    private fun countAllMovies(): Int = mongo.findAll(Movie::class.java).size

    private companion object {
        const val MOVIE_COLLECTION = "movies"
        val TEST_MOVIE_1 = Movie(id = "1", title = "Pulp Fiction")
        val TEST_MOVIE_1_UPDATE = Movie(id = "1", title = "Matrix")
        val TEST_MOVIE_2 = Movie(id = "2", title = "Goodfellas")
        val TEST_MOVIE_3_NOID = Movie(title = "The Godfather")
    }
}
