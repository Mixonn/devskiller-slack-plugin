package pl.allegro.devskiller.domain.assessments

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsInEvaluation
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsNotifier
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsSummary
import pl.allegro.devskiller.domain.assessments.notifier.NoAssessmentsToEvaluate
import pl.allegro.devskiller.domain.assessments.provider.AssessmentsProvider
import pl.allegro.devskiller.domain.assessments.provider.assessmentAlmostTwoHoursAgo
import pl.allegro.devskiller.domain.assessments.provider.assessmentOverTwoHoursAgo
import pl.allegro.devskiller.domain.assessments.provider.assessmentTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.overTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.twoHoursAgo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class NotifierServiceTest {

    private val provider = mockk<AssessmentsProvider>()
    private val notifier = mockk<AssessmentsNotifier>(relaxed = true)
    private val notifierService = NotifierService(notifier, provider)
    private val assessmentsSummary = slot<AssessmentsSummary>()

    @Test
    fun `should notify when no assessments provided`() {
        // given no assessments provided
        every { provider.getAssessmentsToEvaluate() } returns listOf()

        // when
        notifierService.notifyAboutAssessmentsToCheck()

        // then
        verify(exactly = 1) { notifier.notify(capture(assessmentsSummary)) }
        assertTrue(assessmentsSummary.isCaptured)
        assessmentsSummary.captured.also {
            assertEquals(NoAssessmentsToEvaluate, it)
        }
    }

    @Test
    fun `should notify when there are assessments in evaluation`() {
        // given there is an assessment in evaluation
        val assessments = listOf(assessmentTwoHoursAgo)
        every { provider.getAssessmentsToEvaluate() } returns assessments

        // when
        notifierService.notifyAboutAssessmentsToCheck()

        // then
        verify(exactly = 1) { notifier.notify(capture(assessmentsSummary)) }
        assertTrue(assessmentsSummary.isCaptured)
        assessmentsSummary.captured.also {
            if (it !is AssessmentsInEvaluation) fail()
            assertEquals(1, it.remaining)
            assertEquals(twoHoursAgo, it.oldest)
        }
    }

    @Test
    fun `should notify about the oldest assessment if there are multiple of them`() {
        // given there are multiple assessments in evaluation
        val assessments = listOf(assessmentTwoHoursAgo, assessmentOverTwoHoursAgo, assessmentAlmostTwoHoursAgo)
        every { provider.getAssessmentsToEvaluate() } returns assessments

        // when
        notifierService.notifyAboutAssessmentsToCheck()

        // then notified about the oldest one of them
        verify(exactly = 1) { notifier.notify(capture(assessmentsSummary)) }
        assertTrue(assessmentsSummary.isCaptured)
        assessmentsSummary.captured.also {
            if (it !is AssessmentsInEvaluation) fail()
            assertEquals(3, it.remaining)
            assertEquals(overTwoHoursAgo, it.oldest)
        }
    }
}