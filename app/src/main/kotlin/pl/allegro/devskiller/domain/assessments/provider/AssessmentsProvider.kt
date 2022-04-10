package pl.allegro.devskiller.domain.assessments.provider

import java.time.Instant

interface AssessmentsProvider {
    fun getAssessmentsToEvaluate(): List<Assessment>
}
data class Assessment(
    val id: String,
    val testId: TestId,
    val finishDate: Instant
)

data class TestId(val id: String)
