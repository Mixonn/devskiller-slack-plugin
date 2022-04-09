package pl.allegro.devskiller.domain.assessments.provider

import java.time.Instant

interface AssessmentsProvider {
    fun getAssessmentsToEvaluate(): List<Assessment>
}
data class Assessment(
    val id: String,
    val creationDate: Instant,
    val testId: TestId,
    val startDate: Instant,
    val finishDate: Instant
)

data class TestId(val id: String)
