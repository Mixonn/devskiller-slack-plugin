package pl.allegro.devskiller

import java.time.Instant
import pl.allegro.devskiller.config.SlackNotifierConfiguration
import pl.allegro.devskiller.config.SlackNotifierProperties
import pl.allegro.devskiller.config.assessments.CandidatesConfiguration
import pl.allegro.devskiller.config.assessments.DevSkillerProperties
import pl.allegro.devskiller.domain.assessments.AssessmentsToEvaluate

fun main() {
    val configuration = CandidatesConfiguration()
    val candidates = configuration.candidateProvider(
        httpClient = configuration.httpClient(),
        devSkillerConfiguration = DevSkillerProperties("https://api.devskiller.com/", "DevskillerTokenCHANGE")
    ).getCandidatesToEvaluate()

    val slackProperties = SlackNotifierProperties(
        "C01DTCUUH55",
        "xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG"
    )
    val slackConfig = SlackNotifierConfiguration(slackProperties)
    val notifier = slackConfig.slackAssessmentsNotifier()
    notifier.notify(AssessmentsToEvaluate(candidates.size, candidates.minByOrNull { it.latestTestFinishDate!! }!!.latestTestFinishDate!!))
}
