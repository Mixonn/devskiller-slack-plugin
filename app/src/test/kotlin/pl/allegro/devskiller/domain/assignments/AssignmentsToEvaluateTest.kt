package pl.allegro.devskiller.domain.assignments

import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.time.Instant
import kotlin.test.assertEquals

class AssignmentsToEvaluateTest {

    private companion object {
        private val now = Instant.parse("2022-01-23T10:23:00.000Z")
        private val overTwoHoursAgo = Instant.parse("2022-01-23T08:22:00.000Z")
        private val twoHoursAgo = Instant.parse("2022-01-23T08:23:00.000Z")
        private val almostTwoHoursAgo = Instant.parse("2022-01-23T08:24:00.000Z")
        private val twoDaysAgo = Instant.parse("2022-01-21T10:23:00.000Z")
    }

    @TestFactory
    fun `summary should have correctly calculated hours value`() = listOf(
        overTwoHoursAgo to 2,
        almostTwoHoursAgo to 1,
        twoHoursAgo to 2,
        twoDaysAgo to 48
    ).map { (oldest, expectedHours) ->
        dynamicTest("returns $expectedHours when oldest is $oldest") {
            // given
            val assignments = AssignmentsToEvaluate(14, oldest)

            // when
            val summary = assignments.getSummary(now)

            // then
            assertEquals(buildSummary(hours = expectedHours), summary)
        }
    }

    private fun buildSummary(hours: Int) =
        "There are 14 assignments left to evaluate with the longest waiting candidate for $hours hours."
}
