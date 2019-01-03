package io.fbex.flixd.backend.vod.model

data class Stream(
        val type: StreamingType,
        val provider: Provider,
        val urls: StreamingUrls
)

data class StreamingUrls(
        val web: String?,
        val android: String?,
        val ios: String?
)

enum class StreamingType(val value: String) {
    FLATRATE("flatrate"),
    BUY("buy"),
    RENT("rent")
}
