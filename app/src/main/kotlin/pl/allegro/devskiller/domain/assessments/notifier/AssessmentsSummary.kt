package pl.allegro.devskiller.domain.assessments.notifier

import java.time.Duration
import java.time.Instant
import pl.allegro.devskiller.domain.assessments.TestGroup

interface AssessmentsSummary {
    fun getSummary(now: Instant): String
}

data class AssessmentsInEvaluation(
    private val testGroup: TestGroup,
    val remaining: Int,
    val oldest: Instant,
) : AssessmentsSummary {

    override fun getSummary(now: Instant): String {
        val longestWaitingHours = Duration.between(oldest, now).toHours()
        return "${testGroup.getNotificationGroupString()?.plus(" ") ?: ""}There are $remaining `${testGroup.name}` assessments left to evaluate with the longest waiting candidate for *$longestWaitingHours* hours."
    }

    private fun TestGroup.getNotificationGroupString(): String? {
        if(notifyGroupName == null) {
            return null
        }
        return "<!subteam^${notifyGroupName}>"
    }
}

data class NoAssessmentsToEvaluate(private val testGroup: TestGroup): AssessmentsSummary {
    override fun getSummary(now: Instant) = "🎉 There's nothing to evaluate for `${testGroup.name}`. Good job!"
}
