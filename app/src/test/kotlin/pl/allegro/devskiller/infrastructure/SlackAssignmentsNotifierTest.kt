package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsToEvaluate
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackAssignmentsNotifierTest {

    private companion object {
        private val twoDaysAgo = Instant.parse("2022-01-12T21:00:00.000Z")
        private val now = Instant.parse("2022-01-14T21:00:00.000Z")
        private val slackConfig = SlackNotifierConfiguration("channel", "token")
    }

    private val slack = mockk<MethodsClient>(relaxed = true)
    private val notifier = SlackAssignmentsNotifier(slack, slackConfig, FixedTimeProvider(now))

    @Test
    fun `should send notification to slack`() {
        // given
        val assignmentStats = AssignmentsToEvaluate(12, twoDaysAgo)

        // when
        notifier.notify(assignmentStats)

        // then should call slack api
        val request = slot<ChatPostMessageRequest>()
        verify(exactly = 1) { slack.chatPostMessage(capture(request)) }
        confirmVerified(slack)

        // and request should contain certain specific parameters
        assertTrue(request.isCaptured)
        request.captured.also {
            assertEquals(slackConfig.token, it.token)
            assertEquals(slackConfig.channel, it.channel)
            assertEquals(
                "There are 12 assignments left to evaluate with the longest waiting candidate for 48 hours.",
                it.text
            )
        }
    }
}
