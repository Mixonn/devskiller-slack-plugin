package pl.allegro.devskiller

import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.assignments.AssignmentsStatistics
import pl.allegro.devskiller.infrastructure.SlackAssignmentsNotifier

fun main() {
    val slackConfig = SlackNotifierConfiguration(
        "C01DTCUUH55",
        "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG"
    )
    val slackApp = com.slack.api.bolt.App()
    val notifier = SlackAssignmentsNotifier(slackApp.client, slackConfig)
    notifier.notifyAboutCurrentAssignments(AssignmentsStatistics("xdd"))
}
