package pl.allegro.devskiller.config.assessments

import java.util.LinkedList
import java.util.Queue
import pl.allegro.devskiller.domain.assessments.provider.TestId


data class ApplicationConfig(
    var testGroups: TestGroups
)

class TestGroups(
    private val tests: Map<TestDefinition, List<TestId>> = mapOf()
) {
    private val testIds = kotlin.run {
        val result = mutableMapOf<TestId, TestDefinition>()
        tests.forEach { (testDefinition, testsIds) ->
            testsIds.forEach { testId ->
                result[testId] = testDefinition
            }
        }
        return@run result.toMap()
    }

    fun getTestDefinition(testId: TestId): TestDefinition? = testIds[testId]

    fun getAllTests() = tests

    companion object {
        fun fromString(groupsString: String): TestGroups = groupsString.split(";").associate { groupString ->
            val splittedGroupString: Queue<String> = LinkedList(groupString.split(","))
            val testDefinition = TestDefinition(splittedGroupString.poll())
            testDefinition to splittedGroupString.map { TestId(it) }
        }.filter { it.key.name.isNotBlank() }.let { TestGroups(it) }
    }
}

data class TestDefinition(val name: String)
