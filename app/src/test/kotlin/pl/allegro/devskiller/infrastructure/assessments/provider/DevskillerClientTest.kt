package pl.allegro.devskiller.infrastructure.assessments.provider

import io.mockk.every
import io.mockk.mockk
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandler
import java.time.Instant
import org.junit.jupiter.api.assertThrows
import pl.allegro.devskiller.FakeHttpResponse
import pl.allegro.devskiller.ResourceUtils
import pl.allegro.devskiller.config.assessments.AssessmentsConfiguration
import pl.allegro.devskiller.config.assessments.DevSkillerProperties
import pl.allegro.devskiller.domain.assessments.provider.Assessment
import pl.allegro.devskiller.domain.assessments.provider.TestId
import kotlin.test.Test
import kotlin.test.assertEquals


internal class DevskillerClientTest {

    private val httpClient = mockk<HttpClient>()
    private val devSkillerProperties = DevSkillerProperties("http://localhost:1234", "api-token")
    private val objectMapper = AssessmentsConfiguration().objectMapper()
    private val devSkillerClient = DevSkillerClient(httpClient, devSkillerProperties, objectMapper)

    @Test
    fun `should fetch candidates`() {
        // given
        every { httpClient.send(ofType(HttpRequest::class), ofType(BodyHandler::class)) } returns FakeHttpResponse(
            ResourceUtils.getResourceString("invitationsTotal2Size2Page0.json")
        )

        // when call devskiller client
        val result = devSkillerClient.getAssessmentsToEvaluate()

        // then should find 2 elements
        assertEquals(2, result.size)

        // and assessments with id 1 should be parsed correctly
        val expectedAssessmentWithId1 = Assessment(
            id = "assesmentId1",
            creationDate = Instant.parse("2022-04-08T07:15:37Z"),
            testId = TestId("testIdPython"),
            startDate = Instant.parse("2022-04-08T21:28:25Z"),
            finishDate = Instant.parse("2022-04-08T22:46:46Z")
        )
        assertEquals(expectedAssessmentWithId1, result.first { it.id == "assesmentId1" })

        // and assessments with id 2 should be parsed correctly
        val expectedAssessmentWithId2 = Assessment(
            id = "assesmentId2",
            creationDate = Instant.parse("2021-04-08T07:15:37Z"),
            testId = TestId("testIdPython"),
            startDate = Instant.parse("2021-04-08T21:28:25Z"),
            finishDate = Instant.parse("2021-04-08T22:46:46Z")
        )
        assertEquals(expectedAssessmentWithId2, result.first { it.id == "assesmentId2" })
    }

    @Test
    fun `should fetch candidates for multiple pages`() {
        // given
        val jsonResponseWith1ElementFromFirstPage = "invitationsTotal2Size1Page0.json"
        val jsonResponseWith1ElementFromSecondPage = "invitationsTotal2Size1Page1.json"
        every { httpClient.send(match { it.uri().query.contains("page=0") }, ofType(BodyHandler::class)) } returns FakeHttpResponse(
            ResourceUtils.getResourceString(jsonResponseWith1ElementFromFirstPage)
        )
        every { httpClient.send(match { it.uri().query.contains("page=1") }, ofType(BodyHandler::class)) } returns FakeHttpResponse(
            ResourceUtils.getResourceString(jsonResponseWith1ElementFromSecondPage)
        )

        // when call devskiller client
        val result = devSkillerClient.getAssessmentsToEvaluate()

        // then should find 2 elements
        assertEquals(2, result.size)

        // and assessments with id 1 should be parsed correctly
        val expectedAssessmentWithId1 = Assessment(
            id = "assesmentId1",
            creationDate = Instant.parse("2022-04-08T07:15:37Z"),
            testId = TestId("testIdPython"),
            startDate = Instant.parse("2022-04-08T21:28:25Z"),
            finishDate = Instant.parse("2022-04-08T22:46:46Z")
        )
        assertEquals(expectedAssessmentWithId1, result.first { it.id == "assesmentId1" })

        // and assessments with id 2 should be parsed correctly
        val expectedAssessmentWithId2 = Assessment(
            id = "assesmentId2",
            creationDate = Instant.parse("2021-04-08T07:15:37Z"),
            testId = TestId("testIdPython"),
            startDate = Instant.parse("2021-04-08T21:28:25Z"),
            finishDate = Instant.parse("2021-04-08T22:46:46Z")
        )
        assertEquals(expectedAssessmentWithId2, result.first { it.id == "assesmentId2" })
    }

    @Test
    fun `should return empty list if no assessments found`() {
        // given no elements
        val responseBody = """{
            "page": {
                "size": 0,
                "totalElements": 0,
                "totalPages": 0,
                "number": 0
            }
        }"""
        every { httpClient.send(ofType(HttpRequest::class), ofType(BodyHandler::class)) } returns FakeHttpResponse(responseBody)

        // when call devskiller client
        val result = devSkillerClient.getAssessmentsToEvaluate()

        // then should return empty list
        assertEquals(0, result.size)
    }

    @Test
    fun `should throw exception when client did not reponde`() {
        // given response with error

        val responseBodyMessage = "Internal error occured"
        every { httpClient.send(ofType(HttpRequest::class), ofType(BodyHandler::class)) } returns FakeHttpResponse(
            body = responseBodyMessage,
            statusCode = 500
        )

        // when call devskiller client
        val exception = assertThrows<DevskillerHttpException> { devSkillerClient.getAssessmentsToEvaluate() }

        // then exception should be present
        assertEquals(500, exception.statusCode)
    }
}
