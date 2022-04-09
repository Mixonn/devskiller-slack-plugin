package pl.allegro.devskiller.infrastructure.assessments

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import pl.allegro.devskiller.config.assessments.SlackNotifierProperties
import pl.allegro.devskiller.domain.assessments.AssessmentsNotifier
import pl.allegro.devskiller.domain.assessments.AssessmentsToEvaluate
import pl.allegro.devskiller.domain.assessments.NotificationFailedException
import pl.allegro.devskiller.domain.time.TimeProvider

class SlackAssessmentsNotifier(
    private val slackClient: MethodsClient,
    private val slackConfig: SlackNotifierProperties,
    private val timeProvider: TimeProvider,
) : AssessmentsNotifier {

    override fun notify(assessmentsToEvaluate: AssessmentsToEvaluate) {
        val now = timeProvider.getTime()
        val message = assessmentsToEvaluate.getSummary(now)
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
