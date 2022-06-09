package pl.allegro.devskiller.domain.assessments.provider

import java.time.Instant
import pl.allegro.devskiller.domain.assessments.TestGroup
import pl.allegro.devskiller.domain.assessments.notifier.AssessmentsInEvaluation
import pl.allegro.devskiller.domain.assessments.notifier.NoAssessmentsToEvaluate
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.almostTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.overTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.twoHoursAgo

val JAVA_1_TEST_ID = TestId("java1TestId")

val assessmentTwoHoursAgo = Assessment("id twoHoursAgo", JAVA_1_TEST_ID, twoHoursAgo)
val assessmentOverTwoHoursAgo =
    Assessment("id overTwoHoursAgo", JAVA_1_TEST_ID, overTwoHoursAgo)
val assessmentAlmostTwoHoursAgo =
    Assessment("id almostTwoHoursAgo", JAVA_1_TEST_ID, almostTwoHoursAgo)

val simpleTestGroup = TestGroup("java")

fun simpleAssessmentInEvaluationSummary(remaining: Int = 12, oldest: Instant = twoHoursAgo) =
    AssessmentsInEvaluation(remaining = remaining, oldest = oldest, testGroup = simpleTestGroup)

fun simpleEmptyAssessmentSummary() = NoAssessmentsToEvaluate(simpleTestGroup)
