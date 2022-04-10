package pl.allegro.devskiller

import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.slack.api.methods.MethodsClient
import org.junit.jupiter.api.extension.RegisterExtension
import pl.allegro.devskiller.config.assessments.ApplicationConfig
import pl.allegro.devskiller.config.assessments.TestDefinition
import pl.allegro.devskiller.config.assessments.TestGroups
import pl.allegro.devskiller.config.simpleJavaApplicationConfig

abstract class IntegrationTest {

    @JvmField
    @RegisterExtension
    val wiremock: WireMockExtension = WireMockExtension.newInstance().build()

    val slackWiremock = SlackWiremock(wiremock)

    val applicationConfig = simpleJavaApplicationConfig()

    protected fun MethodsClient.injectWiremockUrl() {
        endpointUrlPrefix = "http://localhost:${wiremock.port}/"
    }
}
