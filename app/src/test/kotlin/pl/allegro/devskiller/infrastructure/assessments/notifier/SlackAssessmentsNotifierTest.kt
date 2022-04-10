package pl.allegro.devskiller.infrastructure.assessments.notifier

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import pl.allegro.devskiller.config.assessments.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsInEvaluation
import pl.allegro.devskiller.domain.assessments.notifier.NotificationFailedException
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.now
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.twoDaysAgo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SlackAssessmentsNotifierTest {

    private val slack = mockk<MethodsClient>(relaxed = true)
    private val notifier = SlackNotifierConfiguration(slackProps)
        .slackAssessmentsNotifier(FixedTimeProvider(now), slack)

    @BeforeTest
    fun setup() {
        mockPostMessage(buildPostMessageResponse(ok = true))
    }

    @Test
    fun `should send notification to slack`() {
        // given
        val assessments = AssessmentsInEvaluation(12, twoDaysAgo)

        // when
        notifier.notify(assessments)

        // then should call slack api
        slack.verifyMessageSent()
    }

    @Test
    fun `slack notification should contain specific parameter values`() {
        // given
        val assessments = AssessmentsInEvaluation(12, twoDaysAgo)
        val request = slot<ChatPostMessageRequest>()

        // when
        notifier.notify(assessments)

        // then request should contain certain specific parameters
        slack.verifyMessageSent(request)
        request.captured.also {
            assertEquals(slackProps.token, it.token)
            assertEquals(slackProps.channel, it.channel)
            it shouldHaveText "There are 12 assessments left to evaluate with the longest waiting candidate for 48 hours."
        }
    }

    @Test
    fun `should throw exception when response was not ok`() {
        // given
        val error = "dummy_error"
        val assessments = AssessmentsInEvaluation(12, twoDaysAgo)
        mockPostMessage(buildPostMessageResponse(ok = false, error = error))

        // when
        val notify = { notifier.notify(assessments) }

        // then
        val exception = assertFailsWith(NotificationFailedException::class, notify)
        assertEquals("Slack response was not ok due to the following error: $error", exception.message)
    }

    private fun mockPostMessage(response: ChatPostMessageResponse) {
        every { slack.chatPostMessage(ofType(ChatPostMessageRequest::class)) } returns response
    }
}
