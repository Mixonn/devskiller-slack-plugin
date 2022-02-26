package pl.allegro.devskiller.infrastructure

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import pl.allegro.devskiller.domain.assignments.AssignmentsNotifier
import pl.allegro.devskiller.domain.assignments.AssignmentsStatistics

class SlackAssignmentsNotifier(private val slackClient: MethodsClient) : AssignmentsNotifier {

    override fun notifyAboutCurrentAssignments(assignmentsStats: AssignmentsStatistics) {
        val request = ChatPostMessageRequest.builder()
            .text("pzdrrr")
            .channel("C01DTCUUH55")
            .token("xoxb-1469436098135-3165783184564-mbeMlLdEZbn8TMPvXymcd6DG")
            .build()
        slackClient.chatPostMessage(request)
    }
}
