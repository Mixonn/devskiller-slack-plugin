package pl.allegro.devskiller.config.assessments.devskiller

import java.util.LinkedList
import java.util.Queue
import pl.allegro.devskiller.domain.assessments.TestGroup
import pl.allegro.devskiller.domain.assessments.TestGroups
import pl.allegro.devskiller.domain.assessments.provider.TestId

class TestGroupsBuilder {
    companion object {
        fun fromString(groupsString: String): TestGroups = groupsString.split(";").associate { groupString ->
            val splittedGroupString: Queue<String> = LinkedList(groupString.split(","))
            val testName = splittedGroupString.poll()
            val groupNotifyString = if (splittedGroupString.peek()?.startsWith("@") == true) {
                splittedGroupString.poll().substring(1)
            } else {
                null
            }
            val testDefinition = TestGroup(name = testName, notifyGroupName = groupNotifyString)
            testDefinition to splittedGroupString.map { TestId(it) }
        }.filter { it.key.name.isNotBlank() }.let { TestGroups(it) }
    }
}
