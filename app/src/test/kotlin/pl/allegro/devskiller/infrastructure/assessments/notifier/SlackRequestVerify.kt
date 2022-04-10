package pl.allegro.devskiller.infrastructure.assessments.notifier

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import io.mockk.CapturingSlot
import io.mockk.confirmVerified
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun MethodsClient.verifyMessageSent() {
    verify(exactly = 1) { chatPostMessage(ofType(ChatPostMessageRequest::class)) }
    confirmVerified(this)
}

fun MethodsClient.verifyMessageSent(slot: CapturingSlot<ChatPostMessageRequest>) {
    verify(exactly = 1) { chatPostMessage(capture(slot)) }
    assertTrue(slot.isCaptured)
}

infix fun ChatPostMessageRequest.shouldHaveText(expectedText: String) {
    assertEquals(expectedText, this.text)
}
