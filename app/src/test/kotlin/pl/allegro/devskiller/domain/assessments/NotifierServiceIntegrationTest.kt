package pl.allegro.devskiller.domain.assessments

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.containing
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.slack.api.bolt.App
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import io.mockk.slot
import io.mockk.spyk
import org.junit.jupiter.api.Test
import pl.allegro.devskiller.IntegrationTest
import pl.allegro.devskiller.ResourceUtils
import pl.allegro.devskiller.config.assessments.devskiller.DevSkillerProperties
import pl.allegro.devskiller.config.assessments.devskiller.DevskillerConfiguration
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierConfiguration
import pl.allegro.devskiller.config.simpleJavaApplicationConfig
import pl.allegro.devskiller.domain.time.FixedTimeProvider
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.now
import pl.allegro.devskiller.infrastructure.assessments.notifier.shouldHaveText
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackProps
import pl.allegro.devskiller.infrastructure.assessments.notifier.verifyMessageSent
import kotlin.test.BeforeTest

internal class NotifierServiceIntegrationTest : IntegrationTest() {

    private val applicationConfig = simpleJavaApplicationConfig()

    private val slack = spyk(App().client)
    private val notifier = SlackNotifierConfiguration(slackProps)
        .slackAssessmentsNotifier(FixedTimeProvider(now), slack)
    private val slackNotifyRequest = slot<ChatPostMessageRequest>()

    private val devSkillerProperties = DevSkillerProperties("", "token")

    private val assessmentConfiguration = DevskillerConfiguration(devSkillerProperties)
    private val assessmentsProvider = assessmentConfiguration.assessmentsProvider()

    private val notifierService = NotifierService(notifier, assessmentsProvider, applicationConfig)

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
        slackWiremock.verifyNotificationSent { withRequestBody(containing("assessments")) }

        // and notification has specific message
        slack.verifyMessageSent(slackNotifyRequest)
        slackNotifyRequest.captured shouldHaveText "There are 2 `java` assessments left to evaluate with the longest waiting candidate for *6947* hours."
    }

    @Test
    fun `should notify when there are no assessments in evaluation`() {
        // given
        devskillerWillReturn("/invitations(.*)", devskillerEmptyResponse())
        slackWiremock.stubPostMessage()

        // when
        notifierService.notifyAboutAssessmentsToCheck()

        // then sent Slack notification request
        slackWiremock.verifyNotificationSent()

        // and notification has specific message
        slack.verifyMessageSent(slackNotifyRequest)
        slackNotifyRequest.captured shouldHaveText "🎉 There's nothing to evaluate for `java`. Good job!"
    }

    private fun responseWithTwoInvitations() =
        ok().withBody(ResourceUtils.getResourceString("invitationsTotal2Size2Page0.json"))

    private fun devskillerEmptyResponse() =
        ok().withBody(ResourceUtils.getResourceString("invitationsEmpty.json"))

    private fun devskillerWillReturn(pathPattern: String, response: ResponseDefinitionBuilder) =
        wiremock.stubFor(get(urlMatching(pathPattern)).willReturn(response))
}
