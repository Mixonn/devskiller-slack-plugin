package pl.allegro.devskiller

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import pl.allegro.devskiller.domain.assessments.ApplicationConfig
import pl.allegro.devskiller.config.assessments.devskiller.DevSkillerProperties
import pl.allegro.devskiller.config.assessments.devskiller.DevskillerConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierProperties
import pl.allegro.devskiller.domain.assessments.NotifierService
import pl.allegro.devskiller.infrastructure.assessments.TestGroupsBuilder

fun main(args: Array<String>) {
    val parser = ArgParser("<this_executable>")
    val devskillerToken by parser.option(ArgType.String).required()
    val slackToken by parser.option(ArgType.String).required()
    val slackChannel by parser.option(ArgType.String).required()
    val testGroupsString by parser.option(
        ArgType.String,
        fullName = "testGroups",
        description = """Pass devskiller test groups. Each group is separated by ';'. Every test id is separated by ','.
            |First element is interpreted as group name.
            |Example:
            |--testGroups java,109jc3v3,d232f,md029d;python,d02909
            |will create 2 groups, one Java with ids 109jc3v3,d232f,md029d, 
            |and second "Python" group with test id d02909
        """.trimMargin()
    ).required()
    parser.parse(args)

    val devskillerProperties = DevSkillerProperties("https://api.devskiller.com/", devskillerToken)
    val configuration = DevskillerConfiguration(devskillerProperties)
    val assessmentsProvider = configuration.assessmentsProvider()

    val slackProperties = SlackNotifierProperties(slackChannel, slackToken)
    val slackConfig = SlackNotifierConfiguration(slackProperties)
    val notifier = slackConfig.slackAssessmentsNotifier()

    val notifierService = NotifierService(notifier, assessmentsProvider, ApplicationConfig(TestGroupsBuilder.fromString(testGroupsString)))
    notifierService.notifyAboutAssessmentsToCheck()
}
