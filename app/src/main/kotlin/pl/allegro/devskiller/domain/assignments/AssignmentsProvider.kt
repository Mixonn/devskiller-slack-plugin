package pl.allegro.devskiller.domain.assignments

import java.time.Instant

interface AssignmentsProvider {
    fun getAssignmentsToEvaluate(tests: List<TestId>): List<Assignment>
}

data class TestId(val id: String)
data class Assignment(val testFinishedDate: Instant)
