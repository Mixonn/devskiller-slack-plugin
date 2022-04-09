package pl.allegro.devskiller.infrastructure.assessments

import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandler
import java.time.Instant
import org.junit.jupiter.api.assertThrows
import pl.allegro.devskiller.config.assessments.CandidatesConfiguration
import pl.allegro.devskiller.config.assessments.DevSkillerProperties
import pl.allegro.devskiller.domain.assessments.AssessmentInEvaluation
import pl.allegro.devskiller.domain.assessments.TestId
import kotlin.test.Test
import kotlin.test.assertEquals


internal class DevSkillerClientTest {

    private val httpClient = mockk<HttpClient>()
    private val devSkillerProperties = DevSkillerProperties("http://localhost:1234", "api-token")
    private val objectMapper = CandidatesConfiguration().objectMapper()
    private val devSkillerClient = DevSkillerClient(httpClient, devSkillerProperties, objectMapper)

    private val classLoader = javaClass.classLoader


    @Test
    fun `should fetch candidates`() {
        // given
        val jsonResponseWith2ElementsOnOnePage = "invitationsTotal2Size2Page0.json"
        val responseFile = File(
            classLoader.getResource(jsonResponseWith2ElementsOnOnePage)?.file
                ?: throw java.lang.IllegalArgumentException("Cannot find $jsonResponseWith2ElementsOnOnePage file")
        )
        every { httpClient.send(ofType(HttpRequest::class), ofType(BodyHandler::class)) } returns FakeHttpResponse(responseFile.readText())

        // when call devskiller client
        val result = devSkillerClient.getAssessmentsToEvaluate()

        // then should find 2 elements
        assertEquals(2, result.size)

        // and assessments with id 1 should be parsed correctly
        val expectedAssessmentWithId1 = AssessmentInEvaluation(
            id = "assesmentId1",
            creationDate = Instant.parse("2022-04-08T07:15:37Z"),
            testId = TestId("testIdPython"),
            startDate = Instant.parse("2022-04-08T21:28:25Z"),
            finishDate = Instant.parse("2022-04-08T22:46:46Z")
        )
        assertEquals(expectedAssessmentWithId1, result.first { it.id == "assesmentId1" })

        // and assessments with id 2 should be parsed correctly
        val expectedAssessmentWithId2 = AssessmentInEvaluation(
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
        val responseFirstPageFile = File(
            classLoader.getResource(jsonResponseWith1ElementFromFirstPage)?.file
                ?: throw java.lang.IllegalArgumentException("Cannot find $jsonResponseWith1ElementFromFirstPage file")
        )
        val responseSecondPageFile = File(
            classLoader.getResource(jsonResponseWith1ElementFromSecondPage)?.file
                ?: throw java.lang.IllegalArgumentException("Cannot find $jsonResponseWith1ElementFromSecondPage file")
        )
        every { httpClient.send(match { it.uri().query.contains("page=0") }, ofType(BodyHandler::class)) } returns FakeHttpResponse(
            responseFirstPageFile.readText()
        )
        every { httpClient.send(match { it.uri().query.contains("page=1") }, ofType(BodyHandler::class)) } returns FakeHttpResponse(
            responseSecondPageFile.readText()
        )

        // when call devskiller client
        val result = devSkillerClient.getAssessmentsToEvaluate()

        // then should find 2 elements
        assertEquals(2, result.size)

        // and assessments with id 1 should be parsed correctly
        val expectedAssessmentWithId1 = AssessmentInEvaluation(
            id = "assesmentId1",
            creationDate = Instant.parse("2022-04-08T07:15:37Z"),
            testId = TestId("testIdPython"),
            startDate = Instant.parse("2022-04-08T21:28:25Z"),
            finishDate = Instant.parse("2022-04-08T22:46:46Z")
        )
        assertEquals(expectedAssessmentWithId1, result.first { it.id == "assesmentId1" })

        // and assessments with id 2 should be parsed correctly
        val expectedAssessmentWithId2 = AssessmentInEvaluation(
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
