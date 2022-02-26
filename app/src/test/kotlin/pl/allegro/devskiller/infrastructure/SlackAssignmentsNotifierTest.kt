package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsStatistics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackAssignmentsNotifierTest {

    private companion object {
        private val slackConfig = SlackNotifierConfiguration("channel", "token")
    }

    private val slack = mockk<MethodsClient>(relaxed = true)
    private val notifier = SlackAssignmentsNotifier(slack, slackConfig)

    @Test
    fun `should send notification to slack`() {
        // given
        val assignmentStats = AssignmentsStatistics("xd")

        // when
        notifier.notifyAboutCurrentAssignments(assignmentStats)

        // then should call slack api
        val request = slot<ChatPostMessageRequest>()
        verify(exactly = 1) { slack.chatPostMessage(capture(request)) }
        confirmVerified(slack)

        // and request should contain certain specific parameters
        assertTrue(request.isCaptured)
        request.captured.also {
            assertEquals(it.token, slackConfig.token)
            assertEquals(it.channel, slackConfig.channel)
            assertEquals(it.text, "pzdrr")
        }
    }
}
