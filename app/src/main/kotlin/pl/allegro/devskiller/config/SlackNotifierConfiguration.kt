package pl.allegro.devskiller.config

import com.slack.api.bolt.App
import com.slack.api.methods.MethodsClient
import pl.allegro.devskiller.domain.time.NowTimeProvider
import pl.allegro.devskiller.domain.time.TimeProvider
import pl.allegro.devskiller.infrastructure.SlackAssignmentsNotifier

class SlackNotifierConfiguration(private val properties: SlackNotifierProperties) {

    fun slackAssignmentsNotifier(
        timeProvider: TimeProvider = NowTimeProvider()
    ): SlackAssignmentsNotifier {
        val slackApp = App()
        return slackAssignmentsNotifier(timeProvider, slackApp.client)
    }

    fun slackAssignmentsNotifier(
        timeProvider: TimeProvider,
        slackMethodsClient: MethodsClient,
    ) = SlackAssignmentsNotifier(slackMethodsClient, properties, timeProvider)
}
