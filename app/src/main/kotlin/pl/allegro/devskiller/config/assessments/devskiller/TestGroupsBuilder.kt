package pl.allegro.devskiller.config.assessments.devskiller

import java.util.LinkedList
import java.util.Queue
import pl.allegro.devskiller.domain.assessments.TestGroup
import pl.allegro.devskiller.domain.assessments.TestGroups
import pl.allegro.devskiller.domain.assessments.provider.TestId

class TestGroupsBuilder {
    companion object {
        fun fromString(groupsString: String): TestGroups = groupsString.split(";").associate { groupString ->
            val splitGroupString: Queue<String> = LinkedList(groupString.split(","))
            val testName = splitGroupString.poll()
            val groupNotifyString = if (splitGroupString.peek()?.startsWith("@") == true) {
                splitGroupString.poll().substring(1)
            } else {
                null
            }
            val testDefinition = TestGroup(name = testName, notifyGroupName = groupNotifyString)
            testDefinition to splitGroupString.map { TestId(it) }
        }.filter { it.key.name.isNotBlank() }.let { TestGroups(it) }
    }
}
