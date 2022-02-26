package pl.allegro.devskiller

import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsToEvaluate
import pl.allegro.devskiller.domain.time.NowTimeProvider
import pl.allegro.devskiller.infrastructure.SlackAssignmentsNotifier
import java.time.Instant

fun main() {
    val slackConfig = SlackNotifierConfiguration(
        "C01DTCUUH55",
        "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG"
    )
    val slackApp = com.slack.api.bolt.App()
    val notifier = SlackAssignmentsNotifier(slackApp.client, slackConfig, NowTimeProvider())
    notifier.notify(AssignmentsToEvaluate(15, Instant.parse("2021-03-21T10:23:00.000Z")))
}
