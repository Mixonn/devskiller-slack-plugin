package pl.allegro.devskiller.infrastructure.assessments.notifier

import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.status
import com.slack.api.bolt.App
import com.slack.api.methods.SlackApiException
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import pl.allegro.devskiller.IntegrationTest
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsInEvaluation
import pl.allegro.devskiller.domain.assessments.notifier.NotificationFailedException
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.now
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.twoDaysAgo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SlackAssessmentsNotifierIntegrationTest : IntegrationTest() {

    private val slack = App().client
    private val notifier = SlackNotifierConfiguration(slackProps)
        .slackAssessmentsNotifier(FixedTimeProvider(now), slack)

    @BeforeTest
    fun setup() {
        slack.injectWiremockUrl()
        slackWiremock.stubAuth()
    }

    @Test
    fun `should send notification to slack`() {
        // given
        val stats = AssessmentsInEvaluation(12, twoDaysAgo)
        slackWiremock.stubPostMessage()

        // when
        notifier.notify(stats)

        // then
        slackWiremock.verifyNotificationSent()
    }

    @TestFactory
    fun `should throw exception when slack returns an error`() =
        listOf(400, 500).map { slackResponseStatus ->
            dynamicTest("when slack responds with $slackResponseStatus") {
                // given
                val stats = AssessmentsInEvaluation(12, twoDaysAgo)
                slackWiremock.stubPostMessage(status(slackResponseStatus).withBody(slackErrorResponse))

                // when
                val notify = { notifier.notify(stats) }

                // then
                assertFailsWith(SlackApiException::class, notify)
            }
        }

    @Test
    fun `should throw exception when response was not ok`() {
        // given
        val stats = AssessmentsInEvaluation(12, twoDaysAgo)
        slackWiremock.stubPostMessage(ok().withBody(slackErrorResponse))

        // when
        val notify = { notifier.notify(stats) }

        // then
        assertFailsWith(NotificationFailedException::class, notify)
    }
}
