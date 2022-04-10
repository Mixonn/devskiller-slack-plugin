package pl.allegro.devskiller.config.assessments.devskiller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import pl.allegro.devskiller.domain.assessments.provider.AssessmentsProvider
import pl.allegro.devskiller.infrastructure.assessments.provider.DevSkillerClient
import java.net.http.HttpClient

class DevskillerConfiguration(private val properties: DevSkillerProperties) {

    private fun httpClient(): HttpClient = HttpClient.newBuilder().build()

    fun objectMapper(): ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())

    fun assessmentsProvider(
        httpClient: HttpClient = httpClient()
    ): AssessmentsProvider = DevSkillerClient(httpClient, properties, objectMapper())
}
