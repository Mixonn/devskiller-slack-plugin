package pl.allegro.devskiller

import pl.allegro.devskiller.config.assessments.CandidatesConfiguration
import pl.allegro.devskiller.config.assessments.DevSkillerConfiguration

fun main() {
//    val slackProperties = SlackNotifierProperties(
//        "C01DTCUUH55",
//        "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG"
//    )
//    val slackConfig = SlackNotifierConfiguration(slackProperties)
//    val notifier = slackConfig.slackAssignmentsNotifier()
//    notifier.notify(AssignmentsToEvaluate(15, Instant.parse("2021-03-21T10:23:00.000Z")))
    val configuration = CandidatesConfiguration()
    val candidates = configuration.candidateProvider(
        httpClient = configuration.httpClient(),
        devSkillerConfiguration = DevSkillerConfiguration("https://api.devskiller.com/", "DevskillerTokenCHANGE")
    ).getCandidatesToEvaluate()
    println("test")
}
