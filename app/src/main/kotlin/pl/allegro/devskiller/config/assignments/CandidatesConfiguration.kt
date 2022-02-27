package pl.allegro.devskiller.config.assignments

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.http.HttpClient
import pl.allegro.devskiller.domain.assignments.CandidateProvider
import pl.allegro.devskiller.infrastructure.assignments.DevSkillerClient

class CandidatesConfiguration {
    private val devSkillerClient = HttpClient.newBuilder().build()
    private val devSkillerConfiguration = DevSkillerConfiguration("someUrl")
    val candidateProvider: CandidateProvider = DevSkillerClient(devSkillerClient, devSkillerConfiguration, ObjectMapper())
}
