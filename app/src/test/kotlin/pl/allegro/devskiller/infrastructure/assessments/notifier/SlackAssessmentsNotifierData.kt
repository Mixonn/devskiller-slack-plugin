package pl.allegro.devskiller.infrastructure.assessments.notifier

import com.slack.api.methods.response.chat.ChatPostMessageResponse
import pl.allegro.devskiller.config.assessments.slack.SlackNotifierProperties

const val slackOkResponse = """{ "ok": true }"""
const val slackErrorResponse = """{ "ok": false }"""
const val slackPostMessageUrl = "/chat.postMessage"
val slackProps = SlackNotifierProperties("channel", "token")

fun buildPostMessageResponse(ok: Boolean, error: String? = null): ChatPostMessageResponse {
    val response = ChatPostMessageResponse()
    response.isOk = ok
    response.error = error
    return response
}
