package pl.allegro.devskiller.domain.assignments

import java.time.Instant

interface CandidateProvider {
    fun getCandidatesToEvaluate(tests: List<TestId>): List<Candidate>
}

data class TestId(val id: String)
data class Candidate(val id: String, val latestTestFinishDate: Instant)
