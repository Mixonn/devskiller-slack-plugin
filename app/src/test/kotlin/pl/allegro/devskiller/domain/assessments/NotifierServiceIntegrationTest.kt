package pl.allegro.devskiller.domain.assessments

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.slack.api.bolt.App
import org.junit.jupiter.api.Test
import pl.allegro.devskiller.IntegrationTest
import pl.allegro.devskiller.ResourceUtils
import pl.allegro.devskiller.config.assessments.devskiller.DevSkillerProperties
import pl.allegro.devskiller.config.assessments.devskiller.DevskillerConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.now
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackProps
import kotlin.test.BeforeTest

internal class NotifierServiceIntegrationTest : IntegrationTest() {

    private val slack = App().client
    private val notifier = SlackNotifierConfiguration(slackProps)
        .slackAssessmentsNotifier(FixedTimeProvider(now), slack)

    private val devSkillerProperties = DevSkillerProperties("", "token")

    private val assessmentConfiguration = DevskillerConfiguration()
    private val assessmentsProvider = assessmentConfiguration.assessmentsProvider(devSkillerProperties = devSkillerProperties)

    private val notifierService = NotifierService(notifier, assessmentsProvider)

    @BeforeTest
    fun setup() {
        slack.injectWiremockUrl()
        devSkillerProperties.url = "http://localhost:${wiremock.port}"
        slackWiremock.stubAuth()
    }

    @Test
    fun `should call notifier when assessments were found`() {
        // given
        devskillerWillReturn("/invitations(.*)", responseWithTwoInvitations())
        slackWiremock.stubPostMessage()

        // when
        notifierService.notifyAboutAssessmentsToCheck()

        // then message with notification was sent
        slackWiremock.verifyNotificationSent(requestPatternModifier = { withRequestBody(containing("assessments")) })
    }

    private fun responseWithTwoInvitations() =
        ok().withBody(ResourceUtils.getResourceString("invitationsTotal2Size2Page0.json"))

    private fun devskillerWillReturn(pathPattern: String, response: ResponseDefinitionBuilder) =
        wiremock.stubFor(get(urlMatching(pathPattern)).willReturn(response))
}
