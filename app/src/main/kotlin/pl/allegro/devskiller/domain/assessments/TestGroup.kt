package pl.allegro.devskiller.domain.assessments

import java.util.LinkedList
import java.util.Queue
import pl.allegro.devskiller.domain.assessments.provider.TestId


data class ApplicationConfig(
    var testGroups: TestGroups
)

class TestGroups(
    private val tests: Map<TestGroup, List<TestId>>
) {
    private val testIds = kotlin.run {
        val result = mutableMapOf<TestId, TestGroup>()
        tests.forEach { (testDefinition, testsIds) ->
            testsIds.forEach { testId ->
                result[testId] = testDefinition
            }
        }
        return@run result.toMap()
    }

    fun getTestDefinition(testId: TestId): TestGroup? = testIds[testId]

    fun getAllTests() = tests

    fun groups() = tests.keys
}

data class TestGroup(val name: String)
