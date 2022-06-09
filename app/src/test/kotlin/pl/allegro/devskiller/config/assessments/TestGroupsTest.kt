package pl.allegro.devskiller.config.assessments

import org.junit.jupiter.api.Test
import pl.allegro.devskiller.domain.assessments.TestGroup
import pl.allegro.devskiller.domain.assessments.provider.TestId
import pl.allegro.devskiller.infrastructure.assessments.TestGroupsBuilder
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TestGroupsTest {

    @Test
    fun `should detect 1 group with 3 tests`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,1,2,3")
        // then
        groups.getAllTests().apply {
            assertEquals(1, size)
            assertEquals(
                expected = this[TestGroup("jvm")],
                actual = listOf(TestId("1"), TestId("2"), TestId("3"))
            )
        }
    }

    @Test
    fun `should detect 3 groups with tests`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,1,2,3;python,11;php,100,101")
        // then
        groups.getAllTests().apply {
            assertEquals(3, size)
            assertEquals(
                expected = listOf(TestId("1"), TestId("2"), TestId("3")),
                actual = this[TestGroup("jvm")]
            )
            assertEquals(
                expected = listOf(TestId("11")),
                actual = this[TestGroup("python")]
            )
            assertEquals(
                expected = listOf(TestId("100"), TestId("101")),
                actual = this[TestGroup("php")]
            )
        }
    }

    @Test
    fun `should parse empty groups`() {
        // when
        val groups = TestGroupsBuilder.fromString(";;")
        // then
        groups.getAllTests().apply {
            assertEquals(expected = 0, actual = size)
        }
    }

    @Test
    fun `should return valid testDefinition`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,1;python,11;php,101")
        // then
        assertEquals(
            expected = TestGroup(name = "jvm"),
            actual = groups.getTestDefinition(TestId("1"))
        )
        assertEquals(
            expected = TestGroup(name = "python"),
            actual = groups.getTestDefinition(TestId("11"))
        )
        assertEquals(
            expected = TestGroup(name = "php"),
            actual = groups.getTestDefinition(TestId("101"))
        )
    }

    @Test
    fun `should return null testDefinition whe not found`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,1;python,11;php,101")
        // then
        assertNull(groups.getTestDefinition(TestId("9999")))
    }
}
