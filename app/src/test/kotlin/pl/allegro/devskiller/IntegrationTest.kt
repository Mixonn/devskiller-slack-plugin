package pl.allegro.devskiller

import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.slack.api.methods.MethodsClient
import org.junit.jupiter.api.extension.RegisterExtension

abstract class IntegrationTest {

    @JvmField
    @RegisterExtension
    val wiremock: WireMockExtension = WireMockExtension.newInstance().build()

    val slackWiremock = SlackWiremock(wiremock)

    protected fun MethodsClient.injectWiremockUrl() {
        endpointUrlPrefix = "http://localhost:${wiremock.port}/"
    }
}
