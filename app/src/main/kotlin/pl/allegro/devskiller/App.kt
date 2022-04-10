package pl.allegro.devskiller

import pl.allegro.devskiller.config.assessments.devskiller.DevskillerConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierProperties
import pl.allegro.devskiller.domain.assessments.NotifierService

fun main() {
    val configuration = DevskillerConfiguration()
    val candidateProvider = configuration.assessmentsProvider()

    val slackProperties = SlackNotifierProperties(
        "C01DTCUUH55",
        "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG"
    )
    val slackConfig = SlackNotifierConfiguration(slackProperties)
    val notifier = slackConfig.slackAssessmentsNotifier()
    val notifierService = NotifierService(notifier, candidateProvider)
    notifierService.notifyAboutAssessmentsToCheck()
}
