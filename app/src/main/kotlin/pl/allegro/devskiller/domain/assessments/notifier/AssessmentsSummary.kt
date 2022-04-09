package pl.allegro.devskiller.domain.assessments.notifier

import java.time.Duration
import java.time.Instant

interface AssessmentsSummary {
    fun getSummary(now: Instant): String
}

data class AssessmentsInEvaluation(
    val remaining: Int,
    val oldest: Instant,
) : AssessmentsSummary {

    override fun getSummary(now: Instant): String {
        val longestWaitingHours = Duration.between(oldest, now).toHours()
        return "There are $remaining assessments left to evaluate with the longest waiting candidate for $longestWaitingHours hours."
    }
}

class NoAssessmentsToEvaluate: AssessmentsSummary {

    override fun getSummary(now: Instant): String {
        TODO("Not yet implemented")
    }
}
