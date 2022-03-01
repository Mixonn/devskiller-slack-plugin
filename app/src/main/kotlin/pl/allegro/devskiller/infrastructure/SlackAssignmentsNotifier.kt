package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import pl.allegro.devskiller.config.SlackNotifierProperties
import pl.allegro.devskiller.domain.assignments.AssignmentsNotifier
import pl.allegro.devskiller.domain.assignments.AssignmentsToEvaluate
import pl.allegro.devskiller.domain.assignments.NotificationFailedException
import pl.allegro.devskiller.domain.time.TimeProvider

class SlackAssignmentsNotifier(
    private val slackClient: MethodsClient,
    private val slackConfig: SlackNotifierProperties,
    private val timeProvider: TimeProvider,
) : AssignmentsNotifier {

    override fun notify(assignmentsToEvaluate: AssignmentsToEvaluate) {
        val now = timeProvider.getTime()
        val message = assignmentsToEvaluate.getSummary(now)
        val request = buildRequest(message)
        val response = slackClient.chatPostMessage(request)
        if (!response.isOk) {
            throw NotificationFailedException("Slack response was not ok due to the following error: ${response.error}")
        }
    }

    private fun buildRequest(message: String) =
        ChatPostMessageRequest.builder()
            .text(message)
            .channel(slackConfig.channel)
            .token(slackConfig.token)
            .build()
}
