package pl.allegro.devskiller

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.junit.jupiter.api.extension.RegisterExtension
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackOkResponse

abstract class IntegrationTest {

    @JvmField
    @RegisterExtension
    val wiremock: WireMockExtension = WireMockExtension.newInstance().build()

    protected fun stubAuth(): StubMapping =
        wiremock.stubFor(WireMock.post("/auth.test").willReturn(WireMock.ok().withBody(slackOkResponse)))
}
