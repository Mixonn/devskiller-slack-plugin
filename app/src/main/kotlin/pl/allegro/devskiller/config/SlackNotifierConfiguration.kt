package pl.allegro.devskiller.config

import com.slack.api.bolt.App
import com.slack.api.methods.MethodsClient
import pl.allegro.devskiller.domain.time.NowTimeProvider
import pl.allegro.devskiller.domain.time.TimeProvider
import pl.allegro.devskiller.infrastructure.SlackAssessmentsNotifier

class SlackNotifierConfiguration(private val properties: SlackNotifierProperties) {

    fun slackAssessmentsNotifier(
        timeProvider: TimeProvider = NowTimeProvider()
    ): SlackAssessmentsNotifier {
        val slackApp = App()
        return slackAssessmentsNotifier(timeProvider, slackApp.client)
    }

    fun slackAssessmentsNotifier(
        timeProvider: TimeProvider,
        slackMethodsClient: MethodsClient,
    ) = SlackAssessmentsNotifier(slackMethodsClient, properties, timeProvider)
}
