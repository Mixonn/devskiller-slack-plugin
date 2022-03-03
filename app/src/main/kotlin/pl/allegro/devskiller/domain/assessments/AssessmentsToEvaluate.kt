package pl.allegro.devskiller.domain.assessments

import java.time.Duration
import java.time.Instant

data class AssessmentsToEvaluate(
    val remaining: Int,
    val oldest: Instant,
) {
    fun getSummary(now: Instant): String {
        val longestWaitingHours = Duration.between(oldest, now).toHours()
        return "There are $remaining assessments left to evaluate with the longest waiting candidate for $longestWaitingHours hours."
    }
}
