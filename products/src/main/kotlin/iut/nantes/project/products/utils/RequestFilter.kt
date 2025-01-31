package iut.nantes.project.products.utils

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

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