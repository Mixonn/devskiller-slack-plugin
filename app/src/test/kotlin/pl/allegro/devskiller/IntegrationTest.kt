package pl.allegro.devskiller

import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.junit.jupiter.api.extension.RegisterExtension

abstract class IntegrationTest {

    @JvmField
    @RegisterExtension
    val wiremock: WireMockExtension = WireMockExtension.newInstance().build()
}
