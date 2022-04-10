package pl.allegro.devskiller

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import pl.allegro.devskiller.config.assessments.devskiller.DevSkillerProperties
import pl.allegro.devskiller.config.assessments.devskiller.DevskillerConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierProperties
import pl.allegro.devskiller.domain.assessments.NotifierService

fun main(args: Array<String>) {
    val parser = ArgParser("<this_executable>")
    val slackChannel by parser.option(ArgType.String).required()
    val slackToken by parser.option(ArgType.String).required()
    val devskillerToken by parser.option(ArgType.String).required()
    parser.parse(args)

    val devskillerProperties = DevSkillerProperties("https://api.devskiller.com/", devskillerToken)
    val configuration = DevskillerConfiguration(devskillerProperties)
    val assessmentsProvider = configuration.assessmentsProvider()

    val slackProperties = SlackNotifierProperties(slackChannel, slackToken)
    val slackConfig = SlackNotifierConfiguration(slackProperties)
    val notifier = slackConfig.slackAssessmentsNotifier()

    val notifierService = NotifierService(notifier, assessmentsProvider)
    notifierService.notifyAboutAssessmentsToCheck()
}
