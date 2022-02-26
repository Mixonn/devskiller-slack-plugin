package pl.allegro.devskiller.infrastructure

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
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
import pl.allegro.devskiller.domain.assignments.AssignmentsToEvaluate
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test

class SlackAssignmentsNotifierIntegrationTest : IntegrationTest() {

    private companion object {
        private const val okResponse = """{ "ok": true }"""
        private const val errorResponse = """{ "ok": false }"""
        private const val postMessageUrl = "/chat.postMessage"
        private val twoDaysAgo = Instant.parse("2022-01-12T21:00:00.000Z")
        private val now = Instant.parse("2022-01-14T21:00:00.000Z")
        private val slackConfig = SlackNotifierConfiguration("channel", "token")
        private val stats = AssignmentsToEvaluate(12, twoDaysAgo)
    }

    private val slack = App().client
    private val notifier = SlackAssignmentsNotifier(slack, slackConfig, FixedTimeProvider(now))

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
        notifier.notify(stats)

        // then should send notification once
        val requestMatcher = postRequestedFor(urlEqualTo(postMessageUrl))
        wiremock.verify(1, requestMatcher)
    }

    @TestFactory
    fun `should throw exception when slack returns an error`() =
        listOf(400, 500).map { slackResponseStatus ->
            dynamicTest("when slack responds with $slackResponseStatus") {
                stubPostMessage(status(slackResponseStatus).withBody(errorResponse))
                assertThrows<SlackApiException> {
                    notifier.notify(stats)
                }
            }
        }

    private fun stubAuth() =
        wiremock.stubFor(post("/auth.test").willReturn(ok().withBody(okResponse)))

    private fun stubPostMessage(response: ResponseDefinitionBuilder) =
        wiremock.stubFor(post(postMessageUrl).willReturn(response))
}
