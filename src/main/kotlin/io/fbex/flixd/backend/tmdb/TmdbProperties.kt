package io.fbex.flixd.backend.tmdb

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "tmdb")
class TmdbProperties {

    lateinit var baseUrl: String
    lateinit var apiKey: String
}
