package pl.allegro.devskiller.config.assessments.devskiller

import java.util.LinkedList
import java.util.Queue

data class DevSkillerProperties(
    var url: String,
    var apiToken: String,
    var testGroups: List<TestGroup>
) {
    data class TestGroup(
        var name: String,
        var testIds: List<String>
    ) {
        companion object {
            fun fromString(groupString: String): TestGroup {
                val splitGroup: Queue<String> = LinkedList(groupString.split(","))
                return TestGroup(splitGroup.poll(), splitGroup.toList())
            }
        }
    }
}
