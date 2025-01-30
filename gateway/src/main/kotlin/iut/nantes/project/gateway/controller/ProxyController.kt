package iut.nantes.project.gateway.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException

@RestController
class ProxyController(
    private val webClient: WebClient
) {
    private fun resolveHttpRequestMethod(requestMethod: String) : HttpMethod {
        return when (requestMethod) {
            "GET" -> HttpMethod.GET
            "POST" -> HttpMethod.POST
            "PUT" -> HttpMethod.PUT
            "DELETE" -> HttpMethod.DELETE
            else -> throw ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED)
        }
    }

    private fun resolveHttpResponse(response: ClientResponse): ResponseEntity<*> {
        return ResponseEntity.status(response.statusCode()).body(response.bodyToMono(String::class.java).block())
    }

    @RequestMapping("/**")
    fun proxyRequest(request: HttpServletRequest): ResponseEntity<*> {
        val user = SecurityContextHolder.getContext().authentication.name

        val url = when {
            request.requestURI.startsWith("/api/v1/families") ||
            request.requestURI.startsWith("/api/v1/products") ->
                "http://localhost:8081" // Port: 8081

            request.requestURI.startsWith("/api/v1/contacts") ||
            request.requestURI.startsWith("/api/v1/stores") ->
                "http://localhost:8082" // Port: 8082

            else -> throw ResponseStatusException(HttpStatus.NOT_FOUND)
        } + request.requestURI

        val headers = HttpHeaders()
        headers.set("X-User", user)
        request.headerNames.asSequence().forEach { name ->
            headers.set(name, request.getHeader(name))
        }

        val response = webClient
            .method(resolveHttpRequestMethod(request.method))
            .uri(url)
            .headers { it.addAll(headers) }
            .bodyValue(request.reader.readText())
            .exchange()
            .block()

        if (response != null) {
            return resolveHttpResponse(response)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}