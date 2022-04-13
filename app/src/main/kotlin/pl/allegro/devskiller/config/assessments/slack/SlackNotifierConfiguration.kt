package pl.allegro.devskiller.config.assessments.slack

import com.slack.api.bolt.App
import com.slack.api.methods.MethodsClient
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsNotifier
import pl.allegro.devskiller.domain.time.NowTimeProvider
import pl.allegro.devskiller.domain.time.TimeProvider
import pl.allegro.devskiller.infrastructure.assessments.notifier.SlackAssessmentsNotifier

class SlackNotifierConfiguration(private val properties: SlackNotifierProperties) {

    fun slackAssessmentsNotifier(
        timeProvider: TimeProvider = NowTimeProvider()
    ): AssessmentsNotifier {
        val slackApp = App()
        return slackAssessmentsNotifier(timeProvider, slackApp.client)
    }

    fun slackAssessmentsNotifier(
        timeProvider: TimeProvider,
        slackMethodsClient: MethodsClient,
    ): AssessmentsNotifier = SlackAssessmentsNotifier(slackMethodsClient, properties, timeProvider)
}
