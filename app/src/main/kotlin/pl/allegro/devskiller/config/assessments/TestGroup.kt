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
        tests.forEach { (testDefinition, testsIds) -> testsIds.forEach { testId -> result[testId] = testDefinition } }
        return@run result.toMap()
    }

    fun getTestDefinition(testId: TestId): TestDefinition? = testIds[testId]

    fun getAllTests() = tests

    companion object {
        fun fromString(groupsString: String): TestGroups {
            val testIds = mutableMapOf<TestDefinition, List<TestId>>()
            val groups =  groupsString.split(";").map { groupString ->
                val splitGroup: Queue<String> = LinkedList(groupString.split(","))
                val testDefinition = TestDefinition(splitGroup.poll())
                testIds[testDefinition] = splitGroup.map { TestId(it) }
            }
            return TestGroups(testIds.toMap())
        }
    }
}

data class TestDefinition(val name: String)
