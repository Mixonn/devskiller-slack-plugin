package pl.allegro.devskiller

import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.config.SlackNotifierProperties
import pl.allegro.devskiller.domain.assignments.AssignmentsToEvaluate
import java.time.Instant

fun main() {
    val slackProperties = SlackNotifierProperties(
        "C01DTCUUH55",
        "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG"
    )
    val slackConfig = SlackNotifierConfiguration(slackProperties)
    val notifier = slackConfig.slackAssignmentsNotifier()
    notifier.notify(AssignmentsToEvaluate(15, Instant.parse("2021-03-21T10:23:00.000Z")))
}
