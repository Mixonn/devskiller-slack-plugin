package pl.allegro.devskiller.domain.assignments

import java.time.Instant

interface CandidateProvider {
    fun getCandidatesToEvaluate(): List<Candidate>
}

data class TestId(val id: String)
data class Candidate(val id: String, val assessments: List<Assessment>) {
    val latestTestFinishDate: Instant?
        get() {
            return assessments.filter { it.finishDate != null }.maxByOrNull { it.finishDate!! }?.finishDate
        }
}
data class Assessment(
    val id: String,
    val creationDate: Instant,
    val startDate: Instant?,
    val finishDate: Instant?,
    val testId: TestId
)
