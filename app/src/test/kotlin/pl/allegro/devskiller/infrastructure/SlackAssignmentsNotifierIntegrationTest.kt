package pl.allegro.devskiller.infrastructure

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.and
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.status
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.slack.api.bolt.App
import com.slack.api.methods.SlackApiException
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
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
        private val stats = AssignmentsStatistics("xd")
    }

    private val slack = App().client
    private val notifier = SlackAssignmentsNotifier(slack, slackConfig)

    @BeforeTest
    fun setup() {
        slack.endpointUrlPrefix = "http://localhost:${wiremock.port}/"
        stubAuth()
    }

    @Test
    fun `should send notification to slack`() {
        // given
        stubPostMessage(ok().withBody(okResponse))

        // when
        notifier.notifyAboutCurrentAssignments(stats)

        // then should send notification once
        val requestMatcher = postRequestedFor(urlEqualTo(postMessageUrl))
        wiremock.verify(1, requestMatcher)

        // and notification should contain certain specific parameters
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

    @TestFactory
    fun `should throw exception when slack returns an error`() =
        listOf(400, 500).map { slackResponseStatus ->
            dynamicTest("when slack responds with $slackResponseStatus") {
                stubPostMessage(status(slackResponseStatus))
                assertThrows<SlackApiException> {
                    notifier.notifyAboutCurrentAssignments(stats)
                }
            }
        }

    private fun stubAuth() =
        wiremock.stubFor(post("/auth.test").willReturn(ok().withBody(okResponse)))

    private fun stubPostMessage(response: ResponseDefinitionBuilder) =
        wiremock.stubFor(post(postMessageUrl).willReturn(response))
}
