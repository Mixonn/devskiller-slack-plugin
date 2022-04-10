package pl.allegro.devskiller.config

import pl.allegro.devskiller.config.assessments.ApplicationConfig
import pl.allegro.devskiller.config.assessments.TestDefinition
import pl.allegro.devskiller.config.assessments.TestGroups
import pl.allegro.devskiller.domain.assessments.provider.JAVA_1_TEST_ID

fun simpleJavaApplicationConfig() = ApplicationConfig(
    TestGroups(
    mapOf(Pair(TestDefinition("java"), listOf(JAVA_1_TEST_ID)))
))
