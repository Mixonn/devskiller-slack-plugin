package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsToEvaluate
import pl.allegro.devskiller.domain.assignments.NotificationFailedException
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SlackAssignmentsNotifierTest {

    private val slack = mockk<MethodsClient>(relaxed = true)
    private val notifier = SlackNotifierConfiguration(slackProps)
        .slackAssignmentsNotifier(FixedTimeProvider(now), slack)

    @BeforeTest
    fun setup() {
        mockPostMessage(buildPostMessageResponse(ok = true))
    }

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

    @Test
    fun `should throw exception when response was not ok`() {
        // given
        val error = "dummy_error"
        val assignmentStats = AssignmentsToEvaluate(12, twoDaysAgo)
        mockPostMessage(buildPostMessageResponse(ok = false, error = error))

        // when
        val notify = { notifier.notify(assignmentStats) }

        // then
        val exception = assertFailsWith(NotificationFailedException::class, notify)
        assertEquals("Slack response was not ok due to the following error: $error", exception.message)
    }

    private fun mockPostMessage(response: ChatPostMessageResponse) {
        every { slack.chatPostMessage(ofType(ChatPostMessageRequest::class)) } returns response
    }
}
