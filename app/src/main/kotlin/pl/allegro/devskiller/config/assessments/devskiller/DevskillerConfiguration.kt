package pl.allegro.devskiller.config.assessments.devskiller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.http.HttpClient
import pl.allegro.devskiller.config.assessments.devskiller.DevSkillerProperties
import pl.allegro.devskiller.domain.assessments.provider.AssessmentsProvider
import pl.allegro.devskiller.infrastructure.assessments.provider.DevSkillerClient


class DevskillerConfiguration {
    fun httpClient(): HttpClient = HttpClient.newBuilder().build()

    fun objectMapper(): ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())

    fun devSkillerProperties() = DevSkillerProperties("someUrl", "token")

    fun assessmentsProvider(
        httpClient: HttpClient = httpClient(),
        devSkillerProperties: DevSkillerProperties = devSkillerProperties(),
    ): AssessmentsProvider = DevSkillerClient(httpClient, devSkillerProperties, objectMapper())
}