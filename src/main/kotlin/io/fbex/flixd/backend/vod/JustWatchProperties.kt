package io.fbex.flixd.backend.vod

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "justwatch")
class JustWatchProperties {

    lateinit var baseUrl: String
}
