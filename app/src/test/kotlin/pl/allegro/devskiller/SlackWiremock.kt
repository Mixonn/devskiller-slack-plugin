package pl.allegro.devskiller

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackOkResponse
import pl.allegro.devskiller.infrastructure.assessments.notifier.slackPostMessageUrl

class SlackWiremock(private val wiremock: WireMockExtension) {

    fun stubAuth(): StubMapping =
        wiremock.stubFor(post("/auth.test").willReturn(ok().withBody(slackOkResponse)))

    fun stubPostMessage(response: ResponseDefinitionBuilder = ok().withBody(slackOkResponse)): StubMapping =
        wiremock.stubFor(post(slackPostMessageUrl).willReturn(response))

    fun verifyNotificationSent(
        count: Int = 1,
        requestPatternModifier: RequestPatternBuilder.() -> RequestPatternBuilder = { this },
    ) = wiremock.verify(count, postRequestedFor(urlEqualTo(slackPostMessageUrl)).requestPatternModifier())
}
