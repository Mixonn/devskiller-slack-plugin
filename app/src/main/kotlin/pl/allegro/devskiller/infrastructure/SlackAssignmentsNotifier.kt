package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsNotifier
import pl.allegro.devskiller.domain.assignments.AssignmentsToEvaluate
import pl.allegro.devskiller.domain.time.TimeProvider

class SlackAssignmentsNotifier(
    private val slackClient: MethodsClient,
    private val slackConfig: SlackNotifierConfiguration,
    private val timeProvider: TimeProvider,
) : AssignmentsNotifier {

    override fun notify(assignmentsToEvaluate: AssignmentsToEvaluate) {
        val now = timeProvider.getTime()
        val message = assignmentsToEvaluate.getMessage(now)
        val request = buildRequest(message)
        slackClient.chatPostMessage(request)
    }

    private fun buildRequest(message: String) =
        ChatPostMessageRequest.builder()
            .text(message)
            .channel(slackConfig.channel)
            .token(slackConfig.token)
            .build()
}
