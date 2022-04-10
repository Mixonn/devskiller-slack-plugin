package pl.allegro.devskiller

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import pl.allegro.devskiller.config.assessments.devskiller.DevSkillerProperties
import pl.allegro.devskiller.config.assessments.devskiller.DevskillerConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierProperties
import pl.allegro.devskiller.domain.assessments.NotifierService

private lateinit var notifierService: NotifierService

fun main(args: Array<String>) {
    val devskillerToken: String = readFromEnv("DEVSKILLER_TOKEN")
    val slackToken: String = readFromEnv("SLACK_TOKEN")

    val parser = ArgParser("<this_executable>")
    val slackChannel by parser.option(ArgType.String).required()
    parser.parse(args)

    val devskillerProperties = DevSkillerProperties("https://api.devskiller.com/", devskillerToken)
    val configuration = DevskillerConfiguration(devskillerProperties)
    val assessmentsProvider = configuration.assessmentsProvider()

    val slackProperties = SlackNotifierProperties(slackChannel, slackToken)
    val slackConfig = SlackNotifierConfiguration(slackProperties)
    val notifier = slackConfig.slackAssessmentsNotifier()

    notifierService = NotifierService(notifier, assessmentsProvider)

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}

private fun Application.configureRouting() {
    routing {
        get("/") {
            notifierService.notifyAboutAssessmentsToCheck()
            call.respondText("Hello World!")
        }
    }
}

private fun readFromEnv(variableName: String): String =
    System.getenv(variableName) ?: throw IllegalArgumentException("Missing env var: $variableName")
