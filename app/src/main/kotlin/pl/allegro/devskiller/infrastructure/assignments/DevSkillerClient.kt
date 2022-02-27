package pl.allegro.devskiller.infrastructure.assignments

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant
import pl.allegro.devskiller.config.assignments.DevSkillerConfiguration
import pl.allegro.devskiller.domain.assignments.Candidate
import pl.allegro.devskiller.domain.assignments.CandidateProvider
import pl.allegro.devskiller.domain.assignments.TestId

class DevSkillerClient(
    private val httpClient: HttpClient,
    private val devSkillerConfiguration: DevSkillerConfiguration,
    private val objectMapper: ObjectMapper
) : CandidateProvider {
    override fun getCandidatesToEvaluate(tests: List<TestId>): List<Candidate> {

        val request = HttpRequest.newBuilder()
            .uri(URI(devSkillerConfiguration.url))
            .GET()
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        return response.getCandidatesResponse()
            .filter { it.status == DevSkillerCandidate.CandidateStatus.IN_EVALUATION }
            .map { Candidate(it.id, it.getLatestAssessmentFinishDate()!!) }
    }

    private fun HttpResponse<String>.getCandidatesResponse() =
        objectMapper.readValue<List<DevSkillerCandidate>?>(
            body(),
            objectMapper.typeFactory.constructCollectionType(List::class.java, DevSkillerCandidate::class.java)
        )

    private data class DevSkillerCandidate(
        val id: String,
        val status: CandidateStatus,
        @JsonProperty("_embedded") val assessments: DevSkillerAssessments
    ) {
        enum class CandidateStatus {
            NEW, WAITING_FOR_ANSWERS, IN_EVALUATION, WAITING_FOR_DECISION, ACCEPTED, REJECTED, EXPIRED, CANCELED, ERROR
        }

        fun getLatestAssessmentFinishDate() = assessments.assessments.maxByOrNull { it.finishDate }?.finishDate
    }

    private data class DevSkillerAssessments(val assessments: List<DevSkillerAssessment>)

    private data class DevSkillerAssessment(
        val id: String,
        val creationDate: Instant,
        val startDate: Instant,
        val finishDate: Instant,
    )
}
