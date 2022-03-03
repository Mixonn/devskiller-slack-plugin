package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.response.chat.ChatPostMessageResponse
import pl.allegro.devskiller.config.SlackNotifierProperties
import java.time.Instant

const val okResponse = """{ "ok": true }"""
const val errorResponse = """{ "ok": false }"""
const val postMessageUrl = "/chat.postMessage"
val slackProps = SlackNotifierProperties("channel", "token")

fun buildPostMessageResponse(ok: Boolean, error: String? = null): ChatPostMessageResponse {
    val response = ChatPostMessageResponse()
    response.isOk = ok
    response.error = error
    return response
}
