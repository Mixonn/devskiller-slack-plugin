package pl.allegro.devskiller.infrastructure.assignments

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import pl.allegro.devskiller.config.assignments.DevSkillerConfiguration
import pl.allegro.devskiller.domain.assignments.Assessment
import pl.allegro.devskiller.domain.assignments.Candidate
import pl.allegro.devskiller.domain.assignments.CandidateProvider
import pl.allegro.devskiller.domain.assignments.TestId

class DevSkillerClient(
    private val httpClient: HttpClient,
    private val devSkillerConfiguration: DevSkillerConfiguration,
    private val objectMapper: ObjectMapper
) : CandidateProvider {
    override fun getCandidatesToEvaluate(): List<Candidate> {
        val invitations = getInvitations(20)
        return invitations.toCandidates()
    }

    private fun getInvitations(countPerPage: Int, fromPage: Int = 0): List<Invitation> {
        val request = HttpRequest.newBuilder()
            .uri(URI("${devSkillerConfiguration.url}/invitations?status=IN_EVALUATION&count=$countPerPage&page=$fromPage"))
            .header("Devskiller-Api-Key", devSkillerConfiguration.apiToken)
            .GET()
            .build()
        val response: HttpResponse<String> = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        val invitationsResponse = objectMapper.readTree(response.body())
        val invitations = invitationsResponse.get("_embedded").get("invitations").toList()
            .map { invitationNode ->
                val invitationCandidate: InvitationCandidate = objectMapper.treeToValue(invitationNode.get("_embedded").get("candidate"))
                val assessment: InvitationAssessment = objectMapper.treeToValue(invitationNode.get("_embedded").get("assessment"))
                return@map Invitation(invitationCandidate, assessment)
            }

        val totalPages = invitationsResponse.get("page").get("totalPages").intValue()
        if (totalPages <= fromPage + 1) {
            return invitations
        }
        return invitations + getInvitations(countPerPage, fromPage + 1)
    }


    private fun List<Invitation>.toCandidates() = this.groupBy( { it.candidate }, { it.assessment })
        .map { (candidate, assessments) ->
            return@map Candidate(
                id = candidate.id,
                assessments = assessments.map {
                    Assessment(it.id, it.creationDate, it.startDate, it.finishDate, TestId(it.test.testId()))
                }
            )
        }

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
}
