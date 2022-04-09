package pl.allegro.devskiller.domain.assessments

import java.time.Instant

interface AssessmentsProvider {
    fun getAssessmentsToEvaluate(): List<AssessmentInEvaluation>
}

sealed class Assessment {
    abstract val id: String
    abstract val creationDate: Instant
    abstract val testId: TestId
}

data class AssessmentInEvaluation(
    override val id: String,
    override val creationDate: Instant,
    override val testId: TestId,
    val startDate: Instant?,
    val finishDate: Instant?
) : Assessment()

data class TestId(val id: String)
