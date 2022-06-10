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
            val testGroup = TestGroup(splitGroupString.poll())
            testGroup to splitGroupString.map { TestId(it) }
        }.filter { it.key.name.isNotBlank() }.let { TestGroups(it) }
    }
}
