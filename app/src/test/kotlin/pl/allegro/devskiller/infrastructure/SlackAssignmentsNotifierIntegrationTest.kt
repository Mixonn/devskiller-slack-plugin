package pl.allegro.devskiller.infrastructure

import com.github.tomakehurst.wiremock.client.WireMock.and
import com.github.tomakehurst.wiremock.client.WireMock.any
import com.github.tomakehurst.wiremock.client.WireMock.anyUrl
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.slack.api.bolt.App
import pl.allegro.devskiller.IntegrationTest
import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsStatistics
import kotlin.test.BeforeTest
import kotlin.test.Test

class SlackAssignmentsNotifierIntegrationTest : IntegrationTest() {

    private companion object {
        private const val okResponse = """{ "ok": true }"""
        private const val postMessageUrl = "/chat.postMessage"
        private val slackConfig = SlackNotifierConfiguration("channel", "token")
    }

    private val slack = App().client
    private val notifier = SlackAssignmentsNotifier(slack, slackConfig)

    @BeforeTest
    fun setup() {
        slack.endpointUrlPrefix = "http://localhost:${wiremock.port}/"
        wiremock.stubFor(any(anyUrl()).willReturn(ok().withBody(okResponse)))
    }

    @Test
    fun `should send notification to slack`() {
        // given
        val stats = AssignmentsStatistics("xd")

        // when
        notifier.notifyAboutCurrentAssignments(stats)

        // then should send notification once
        val requestMatcher = postRequestedFor(urlEqualTo(postMessageUrl))
        wiremock.verify(1, requestMatcher)

        // and notification should contain certain fields
        wiremock.verify(
            requestMatcher
                .withHeader("authorization", equalTo("Bearer ${slackConfig.token}"))
                .withRequestBody(
                    and(
                        containing("channel=${slackConfig.channel}"),
                        containing("text=pzdrr"),
                    )
                )
        )
    }
}
