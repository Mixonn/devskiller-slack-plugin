package pl.allegro.devskiller.config

import pl.allegro.devskiller.domain.assessments.ApplicationConfig
import pl.allegro.devskiller.domain.assessments.TestGroup
import pl.allegro.devskiller.domain.assessments.TestGroups
import pl.allegro.devskiller.domain.assessments.provider.JAVA_1_TEST_ID

fun simpleJavaApplicationConfig() = ApplicationConfig(
    TestGroups(
        mapOf(TestGroup("java") to listOf(JAVA_1_TEST_ID))
    )
)
