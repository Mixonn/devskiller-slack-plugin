package pl.allegro.devskiller

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Optional
import javax.net.ssl.SSLSession

class FakeHttpResponse(
    private val body: String = "",
    private val statusCode: Int = 200,
    private val headers: HttpHeaders? = null
) : HttpResponse<String> {
    override fun statusCode(): Int = statusCode

    override fun request(): HttpRequest {
        return HttpRequest.newBuilder().build()
    }

    override fun previousResponse(): Optional<HttpResponse<String>> = Optional.empty()

    override fun headers(): HttpHeaders {
        return headers ?: HttpHeaders.of(mapOf()) { _, _ -> true }
    }

    override fun body(): String {
        return body;
    }

    override fun sslSession(): Optional<SSLSession> = Optional.empty()

    override fun uri(): URI = URI.create("http://localhost")

    override fun version(): HttpClient.Version = HttpClient.Version.HTTP_1_1
}
