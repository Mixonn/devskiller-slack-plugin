package pl.allegro.devskiller.infrastructure.assessments

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import pl.allegro.devskiller.config.assessments.DevSkillerProperties
import pl.allegro.devskiller.domain.assessments.AssessmentInEvaluation
import pl.allegro.devskiller.domain.assessments.AssessmentsProvider
import pl.allegro.devskiller.domain.assessments.TestId

class DevSkillerClient(
    private val httpClient: HttpClient,
    private val devSkillerConfiguration: DevSkillerProperties,
    private val objectMapper: ObjectMapper
) : AssessmentsProvider {
    override fun getAssessmentsToEvaluate(): List<AssessmentInEvaluation> {
        val invitations = getPendingInvitations(20)
        return invitations.map { it.assessment.toAssessment() }
    }

    private fun getPendingInvitations(countPerPage: Int, fromPage: Int = 0): List<Invitation> {
        val request = HttpRequest.newBuilder()
            .uri(URI("${devSkillerConfiguration.url}/invitations?status=IN_EVALUATION&count=$countPerPage&page=$fromPage"))
            .header(DEVSKILLER_API_KEY_HEADER, devSkillerConfiguration.apiToken)
            .GET()
            .build()
        val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw DevskillerHttpException(response.statusCode(), response.body())
        }
        val invitationsResponse = objectMapper.readTree(response.body())
        val invitations = invitationsResponse.get("_embedded")
            ?.get("invitations")
            ?.map { invitationNode -> invitationNode.toInvitation() } ?: listOf()

        val totalPages = invitationsResponse.get("page")
            ?.get("totalPages")
            ?.intValue() ?: throw AssertionError("There is no page structure in received document")

        if (totalPages <= fromPage + 1) {
            return invitations
        }
        return invitations + getPendingInvitations(countPerPage, fromPage + 1)
    }

    private fun JsonNode.toInvitation(): Invitation {
        val invitationCandidate: InvitationCandidate = objectMapper.treeToValue(this.get("_embedded").get("candidate"))
        val assessment: InvitationAssessment = objectMapper.treeToValue(this.get("_embedded").get("assessment"))
        return Invitation(invitationCandidate, assessment)
    }

    private fun InvitationAssessment.toAssessment() =
        AssessmentInEvaluation(
            id = id,
            creationDate = creationDate,
            testId = TestId(test.testId()),
            startDate = startDate,
            finishDate = finishDate
        )

    data class Invitation(val candidate: InvitationCandidate, val assessment: InvitationAssessment)
    data class InvitationAssessment(
        val id: String,
        val creationDate: Instant,
        val startDate: Instant?,
        val finishDate: Instant?,
        @JsonProperty("_embedded") val test: InvitationTestWrapper
    )

    data class InvitationTestWrapper(val test: InvitationTest) {
        fun testId() = test.id
    }

    data class InvitationTest(val id: String)
    data class InvitationCandidate(val id: String)

    companion object {
        const val DEVSKILLER_API_KEY_HEADER = "Devskiller-Api-Key"
    }
}

class DevskillerHttpException(val statusCode: Int, message: String)
    : RuntimeException("Errror when calling devskiller client. Status code: $statusCode, message: $message")
