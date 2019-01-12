package io.fbex.flixd.backend.tmdb

import classification.IntegrationTest
import com.nhaarman.mockitokotlin2.given
import io.fbex.flixd.backend.tmdb.model.TmdbSearchResult
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromObject
import reactor.core.publisher.Mono

@IntegrationTest
@AutoConfigureWebTestClient
internal class TmdbApiTests {

    @Autowired
    private lateinit var webClient: WebTestClient

    @MockBean
    private lateinit var tmdbWebClient: TmdbWebClient

    @Test
    fun `search movie`() {
        val searchResult = TmdbSearchResult(
            page = 1,
            total_pages = 1,
            total_results = 1,
            results = listOf(MOVIE_PULP_FICTION_SEARCH)
        )

        given { tmdbWebClient.searchMovies(QUERY) }
            .willReturn(Mono.just(searchResult))

        webClient.post().uri("/tmdb/search")
            .accept(APPLICATION_JSON_UTF8)
            .contentType(APPLICATION_JSON_UTF8)
            .body(fromObject(SearchRequest(QUERY)))
            .exchange()
            .expectStatus().isOk
            .expectBody(TmdbSearchResult::class.java)
    }

    @Test
    fun `get movie by id`() {
        val expectedJson = """
            {
                "adult": false,
                "backdrop_path": "/4cDFJr4HnXN5AdPw4AKrmLlMWdO.jpg",
                "budget": 8000000,
                "genres": [
                    {
                        "id": 53,
                        "name": "Thriller"
                    },
                    {
                        "id": 80,
                        "name": "Crime"
                    }
                ],
                "id": 680,
                "imdb_id": "tt0110912",
                "original_language": "en",
                "original_title": "Pulp Fiction",
                "overview": "A burger-loving hit man, his philosophical partner, a drug-addled gangster's moll and a washed-up boxer converge in this sprawling, comedic crime caper. Their adventures unfurl in three stories that ingeniously trip back and forth in time.",
                "popularity": 35.143,
                "poster_path": "/dM2w364MScsjFf8pfMbaWUcWrR.jpg",
                "production_companies": [
                    {
                        "id": 14,
                        "logo_path": "/m6AHu84oZQxvq7n1rsvMNJIAsMu.png",
                        "name": "Miramax",
                        "origin_country": "US"
                    },
                    {
                        "id": 59,
                        "logo_path": "/yH7OMeSxhfP0AVM6iT0rsF3F4ZC.png",
                        "name": "A Band Apart",
                        "origin_country": "US"
                    },
                    {
                        "id": 216,
                        "name": "Jersey Films",
                        "origin_country": ""
                    }
                ],
                "production_countries": [
                    {
                        "iso_3166_1": "US",
                        "name": "United States of America"
                    }
                ],
                "release_date": "1994-09-10",
                "revenue": 213928762,
                "runtime": 154,
                "spoken_languages": [
                    {
                        "iso_639_1": "en",
                        "name": "English"
                    },
                    {
                        "iso_639_1": "es",
                        "name": "Español"
                    },
                    {
                        "iso_639_1": "fr",
                        "name": "Français"
                    }
                ],
                "status": "Released",
                "tagline": "Just because you are a character doesn't mean you have character.",
                "title": "Pulp Fiction",
                "video": false,
                "vote_average": 8.4,
                "vote_count": 13606
            }
        """.trimIndent()

        given { tmdbWebClient.getMovieDetails(ID) }
            .willReturn(Mono.just(MOVIE_PULP_FICTION_DETAILS))

        buildMovieDetailRequest()
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json(expectedJson)
    }

    @Test
    fun `returns status NOT_FOUND if tmdb doesn't find a matching movie`() {
        given { tmdbWebClient.getMovieDetails(ID) }
            .willReturn(Mono.empty())

        buildMovieDetailRequest()
            .exchange()
            .expectStatus().isNotFound
    }

    private fun buildMovieDetailRequest() = webClient.get().uri("/tmdb/movie/680").accept(APPLICATION_JSON_UTF8)

    private companion object {
        const val QUERY = "pulp fiction"
        const val ID = 680
    }
}
