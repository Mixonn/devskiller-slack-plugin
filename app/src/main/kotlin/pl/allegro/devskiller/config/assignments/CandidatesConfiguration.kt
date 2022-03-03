package pl.allegro.devskiller.config.assignments

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.http.HttpClient
import pl.allegro.devskiller.domain.assignments.CandidateProvider
import pl.allegro.devskiller.infrastructure.assignments.DevSkillerClient


class CandidatesConfiguration {
    fun httpClient() = HttpClient.newBuilder().build()

    fun objectMapper() = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())

    fun devSkillerConfiguration() = DevSkillerConfiguration("someUrl", "token")

    fun candidateProvider(
        httpClient: HttpClient,
        devSkillerConfiguration: DevSkillerConfiguration,
    ): CandidateProvider = DevSkillerClient(httpClient, devSkillerConfiguration, objectMapper())
}
