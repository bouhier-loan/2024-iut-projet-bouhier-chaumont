package iut.nantes.project.stores.utils

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestFilter: OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.getHeader("X-User") == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.write("Missing X-User header")
            return
        }
        filterChain.doFilter(request, response)
    }
}