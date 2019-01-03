package io.fbex.flixd.backend.vod.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProviderTests {

    @Test
    fun `maps provider ids to enum`() {
        assertThat(Provider.forProviderId(8)).isEqualTo(Provider.NETFLIX)
        assertThat(Provider.forProviderId(9)).isEqualTo(Provider.AMAZON_PRIME)
        assertThat(Provider.forProviderId(29)).isEqualTo(Provider.SKY_GO)
        assertThat(Provider.forProviderId(30)).isEqualTo(Provider.SKY_TICKET)
        assertThat(Provider.forProviderId(1)).isEqualTo(Provider.UNKNOWN)
    }
}
