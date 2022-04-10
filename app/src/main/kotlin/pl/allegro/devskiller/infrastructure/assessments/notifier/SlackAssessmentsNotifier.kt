package pl.allegro.devskiller.infrastructure.assessments.notifier

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierProperties
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsNotifier
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsSummary
import pl.allegro.devskiller.domain.assessments.notifier.NotificationFailedException
import pl.allegro.devskiller.domain.time.TimeProvider

internal class SlackAssessmentsNotifier(
    private val slackClient: MethodsClient,
    private val slackConfig: SlackNotifierProperties,
    private val timeProvider: TimeProvider,
) : AssessmentsNotifier {

    override fun notify(assessmentsSummary: AssessmentsSummary) {
        val message = assessmentsSummary.getSummary(timeProvider.getTime())
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
