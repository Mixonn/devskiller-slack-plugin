package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsNotifier
import pl.allegro.devskiller.domain.assignments.AssignmentsStatistics

class SlackAssignmentsNotifier(
    private val slackClient: MethodsClient,
    private val slackConfig: SlackNotifierConfiguration,
) : AssignmentsNotifier {

    override fun notifyAboutCurrentAssignments(assignmentsStats: AssignmentsStatistics) {
        val request = buildRequest("pzdrr")
        slackClient.chatPostMessage(request)
    }

    private fun buildRequest(message: String) =
        ChatPostMessageRequest.builder()
            .text(message)
            .channel(slackConfig.channel)
            .token(slackConfig.token)
            .build()
}
