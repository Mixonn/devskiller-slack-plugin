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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackAssignmentsNotifierTest {

    private val slack = mockk<MethodsClient>(relaxed = true)
    private val notifier = SlackNotifierConfiguration(slackProps)
        .slackAssignmentsNotifier(FixedTimeProvider(now), slack)

    @Test
    fun `should send notification to slack`() {
        // given
        val assignmentStats = AssignmentsToEvaluate(12, twoDaysAgo)

        // when
        notifier.notify(assignmentStats)

        // then should call slack api
        verify(exactly = 1) { slack.chatPostMessage(ofType(ChatPostMessageRequest::class)) }
        confirmVerified(slack)
    }

    @Test
    fun `slack notification should contain specific parameter values`() {
        // given
        val assignmentStats = AssignmentsToEvaluate(12, twoDaysAgo)
        val request = slot<ChatPostMessageRequest>()

        // when
        notifier.notify(assignmentStats)

        // then request should contain certain specific parameters
        verify { slack.chatPostMessage(capture(request)) }
        assertTrue(request.isCaptured)
        request.captured.also {
            assertEquals(slackProps.token, it.token)
            assertEquals(slackProps.channel, it.channel)
            assertEquals(
                "There are 12 assignments left to evaluate with the longest waiting candidate for 48 hours.",
                it.text
            )
        }

    }
}
