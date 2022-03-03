package pl.allegro.devskiller.domain.assessments

import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.almostTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.now
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.overTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.twoDaysAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.twoHoursAgo
import kotlin.test.assertEquals

class AssessmentsToEvaluateTest {

    @TestFactory
    fun `summary should have correctly calculated hours value`() = listOf(
        overTwoHoursAgo to 2,
        almostTwoHoursAgo to 1,
        twoHoursAgo to 2,
        twoDaysAgo to 48
    ).map { (oldest, expectedHours) ->
        dynamicTest("returns $expectedHours when oldest is $oldest") {
            // given
            val assessments = AssessmentsToEvaluate(14, oldest)

            // when
            val summary = assessments.getSummary(now)

            // then
            assertEquals(buildSummary(hours = expectedHours), summary)
        }
    }

    private fun buildSummary(hours: Int) =
        "There are 14 assessments left to evaluate with the longest waiting candidate for $hours hours."
}
