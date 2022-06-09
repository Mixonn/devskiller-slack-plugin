package pl.allegro.devskiller.config.assessments

import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.ValueSource
import pl.allegro.devskiller.domain.assessments.TestGroup
import pl.allegro.devskiller.domain.assessments.provider.TestId
import pl.allegro.devskiller.config.assessments.devskiller.TestGroupsBuilder
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class TestGroupsBuilderTest {

    @Test
    fun `should detect 1 group with 3 tests`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,1,2,3")
        // then
        groups.getAllGroupedTests().apply {
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
        groups.getAllGroupedTests().apply {
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
        groups.getAllGroupedTests().apply {
            assertEquals(expected = 0, actual = size)
        }
    }

    @Test
    fun `should return valid testGroup`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,1;python,11;php,101")
        // then
        assertEquals(
            expected = TestGroup(name = "jvm"),
            actual = groups.getTestGroup(TestId("1"))
        )
        assertEquals(
            expected = TestGroup(name = "python"),
            actual = groups.getTestGroup(TestId("11"))
        )
        assertEquals(
            expected = TestGroup(name = "php"),
            actual = groups.getTestGroup(TestId("101"))
        )
    }

    @Test
    fun `should detect valid notify group name`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,@all,1")
        // then
        assertEquals(
            expected = TestGroup(name = "jvm", "all"),
            actual = groups.getTestGroup(TestId("1"))
        )
    }

    @Test
    fun `should return null testGroup whe not found`() {
        // when
        val groups = TestGroupsBuilder.fromString("jvm,1;python,11;php,101")
        // then
        assertNull(groups.getTestGroup(TestId("9999")))
    }
}
