package pl.allegro.devskiller.domain.assignments

import java.time.Duration
import java.time.Instant

data class AssignmentsToEvaluate(
    val remaining: Int,
    val eldest: Instant,
) {
    fun getMessage(now: Instant): String {
        val longestWaitingHours = Duration.between(eldest, now).toHours()
        return "There are $remaining assignments left to evaluate with the longest waiting candidate for $longestWaitingHours hours."
    }
}
