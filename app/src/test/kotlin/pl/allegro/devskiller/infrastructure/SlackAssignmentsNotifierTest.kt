package pl.allegro.devskiller.infrastructure

import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.slack.api.bolt.App
import org.junit.jupiter.api.extension.RegisterExtension
import pl.allegro.devskiller.domain.assignments.AssignmentsStatistics
import kotlin.test.BeforeTest
import kotlin.test.Test

class SlackAssignmentsNotifierTest {

    private companion object {
        private const val okResponse = """{ "ok": true }"""
        private const val postMessageUrl = "/chat.postMessage"
    }

    @JvmField
    @RegisterExtension
    val wiremock: WireMockExtension = WireMockExtension.newInstance().build()

    private val slack = App().client
    private val notifier = SlackAssignmentsNotifier(slack)

    @BeforeTest
    fun setup() {
        slack.endpointUrlPrefix = "http://localhost:${wiremock.port}/"
        wiremock.stubFor(any(anyUrl()).willReturn(ok().withBody(okResponse)))
    }

    @Test
    fun `should send notification to slack`() {
        val stats = AssignmentsStatistics("xd")
        notifier.notifyAboutCurrentAssignments(stats)
        wiremock.verify(1, postRequestedFor(urlEqualTo(postMessageUrl)))
    }
}
