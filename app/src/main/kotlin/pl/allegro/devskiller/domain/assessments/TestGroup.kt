package pl.allegro.devskiller.domain.assessments

import pl.allegro.devskiller.domain.assessments.provider.TestId


data class ApplicationConfig(
    var testGroups: TestGroups
)

class TestGroups(
    private val tests: Map<TestGroup, List<TestId>>
) {
    private val testIds = kotlin.run {
        val result = mutableMapOf<TestId, TestGroup>()
        tests.forEach { (testGroup, testsIds) ->
            testsIds.forEach { testId ->
                result[testId] = testGroup
            }
        }
        return@run result.toMap()
    }

    fun getTestGroup(testId: TestId): TestGroup? = testIds[testId]

    fun groups() = tests.keys

    fun getAllGroupedTests() = tests
}

data class TestGroup(val name: String, val notifyGroupName: String? = null)
