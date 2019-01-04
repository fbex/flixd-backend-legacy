package io.fbex.flixd.backend

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebFluxConfiguration : WebFluxConfigurer {

    /**
     * Manual configuration since application properties don't work.
     * I.e. "spring.jackson.default-property-inclusion: non_null"
     */
    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(
            Jackson2JsonEncoder(
                Jackson2ObjectMapperBuilder
                    .json()
                    .serializationInclusion(NON_NULL)
                    .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
                    .build()
            )
        )
    }
}
