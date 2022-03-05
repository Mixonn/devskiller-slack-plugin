package pl.allegro.devskiller.config.assessments

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.http.HttpClient
import pl.allegro.devskiller.domain.assessments.CandidateProvider
import pl.allegro.devskiller.infrastructure.assessments.DevSkillerClient


class CandidatesConfiguration {
    fun httpClient() = HttpClient.newBuilder().build()

    fun objectMapper() = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())

    fun devSkillerConfiguration() = DevSkillerProperties("someUrl", "token")

    fun candidateProvider(
        httpClient: HttpClient,
        devSkillerConfiguration: DevSkillerProperties,
    ): CandidateProvider = DevSkillerClient(httpClient, devSkillerConfiguration, objectMapper())
}
