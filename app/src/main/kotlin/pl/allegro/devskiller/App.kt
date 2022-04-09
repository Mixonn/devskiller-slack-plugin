package pl.allegro.devskiller

import pl.allegro.devskiller.config.assessments.SlackNotifierConfiguration
import pl.allegro.devskiller.config.assessments.SlackNotifierProperties
import pl.allegro.devskiller.config.assessments.CandidatesConfiguration
import pl.allegro.devskiller.config.assessments.DevSkillerProperties
import pl.allegro.devskiller.domain.assessments.NotifierService

fun main() {
    val configuration = CandidatesConfiguration()
    val candidateProvider = configuration.candidateProvider(
        httpClient = configuration.httpClient(),
        devSkillerConfiguration = DevSkillerProperties("https://api.devskiller.com/", "CHANGE_ME")
    )

    val slackProperties = SlackNotifierProperties(
        "C01DTCUUH55",
        "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG"
    )
    val slackConfig = SlackNotifierConfiguration(slackProperties)
    val notifier = slackConfig.slackAssessmentsNotifier()
    val notifierService = NotifierService(notifier, candidateProvider)
    notifierService.notifyAboutAssessmentsToCheck()
}
