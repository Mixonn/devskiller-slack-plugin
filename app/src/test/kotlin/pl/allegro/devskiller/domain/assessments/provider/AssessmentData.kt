package pl.allegro.devskiller.domain.assessments.provider

import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.almostTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.overTwoHoursAgo
import pl.allegro.devskiller.domain.time.FixedTimeProvider.Companion.twoHoursAgo

private val testId = TestId("testId")
val assessmentTwoHoursAgo = Assessment("id twoHoursAgo", testId, twoHoursAgo)
val assessmentOverTwoHoursAgo =
    Assessment("id overTwoHoursAgo", testId, overTwoHoursAgo)
val assessmentAlmostTwoHoursAgo =
    Assessment("id overTwoHoursAgo", testId, almostTwoHoursAgo)
