package pl.allegro.devskiller.domain.assessments

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.slack.api.bolt.App
import org.junit.jupiter.api.Test
import pl.allegro.devskiller.IntegrationTest
import pl.allegro.devskiller.ResourceUtils
import pl.allegro.devskiller.config.assessments.AssessmentsConfiguration
import pl.allegro.devskiller.config.assessments.DevSkillerProperties
import pl.allegro.devskiller.config.assessments.SlackNotifierConfiguration
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackOkResponse
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackPostMessageUrl
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackProps
import kotlin.test.BeforeTest

internal class NotifierServiceIntegrationTest : IntegrationTest() {

    private val slack = App().client
    private val notifier = SlackNotifierConfiguration(slackProps)
        .slackAssessmentsNotifier(FixedTimeProvider(FixedTimeProvider.now), slack)

    private val assessmentConfiguration = AssessmentsConfiguration()
    private val devSkillerProperties = DevSkillerProperties("", "token")
    private val assessmentsProvider = assessmentConfiguration.assessmentsProvider(devSkillerProperties = devSkillerProperties)

    private val notifierService = NotifierService(notifier, assessmentsProvider)

    @BeforeTest
    fun setup() {
        slack.endpointUrlPrefix = "http://localhost:${wiremock.port}/"
        devSkillerProperties.url = "http://localhost:${wiremock.port}"
        stubAuth()
    }

    @Test
    fun `should call notifier when assessments were found`() {
        // given
        devskillerWillReturn("/invitations(.*)", responseWithTwoInvitations())
        slackWillReturn(ok().withBody(slackOkResponse))

        // when
        notifierService.notifyAboutAssessmentsToCheck()

        // then message with notification was sent
        verifyNotificationSent(1)
    }

    private fun responseWithTwoInvitations() = ok().withBody(ResourceUtils.getResourceString("invitationsTotal2Size2Page0.json"))

    private fun slackWillReturn(response: ResponseDefinitionBuilder) =
        wiremock.stubFor(post(slackPostMessageUrl).willReturn(response))

    private fun devskillerWillReturn(pathPattern: String, response: ResponseDefinitionBuilder) =
        wiremock.stubFor(get(urlMatching(pathPattern)).willReturn(response))

    private fun verifyNotificationSent(count: Int = 1) =
        wiremock.verify(count, postRequestedFor(urlEqualTo(slackPostMessageUrl)).withRequestBody(containing("assessments")))
}
