package pl.allegro.devskiller.domain.assessments.notifier

import java.time.Duration
import java.time.Instant
import pl.allegro.devskiller.config.assessments.TestDefinition

interface AssessmentsSummary {
    fun getSummary(now: Instant): String
}

data class AssessmentsInEvaluation(
    private val testDefinition: TestDefinition,
    val remaining: Int,
    val oldest: Instant,
) : AssessmentsSummary {

    override fun getSummary(now: Instant): String {
        val longestWaitingHours = Duration.between(oldest, now).toHours()
        return "There are $remaining `${testDefinition.name}` assessments left to evaluate with the longest waiting candidate for *$longestWaitingHours* hours."
    }
}

data class NoAssessmentsToEvaluate(private val testDefinition: TestDefinition): AssessmentsSummary {
    override fun getSummary(now: Instant) = "🎉 There's nothing to evaluate for `${testDefinition.name}`. Good job!"
}
