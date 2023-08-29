package skeleton.app.support.access.auth.basic.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import skeleton.app.configuration.constants.Endpoints.AUTH
import skeleton.app.support.access.session.SessionService

@Component
class JwtAuthFilter(
        private val jwtService: JwtService,
        private val userDetailsService: UserDetailsService,
        private val sessionService: SessionService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        if (request.servletPath.contains(AUTH)) {
            filterChain.doFilter(request, response);
            return;
        }

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)
        val username = jwtService.extractUsername(jwt)

        if (SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)

            val isTokenValid = sessionService.findByToken(jwt)
                    .map { it!!.expired && !it.revoked }
                    ?.orElse(false)

            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid!!) {
                val authenticationToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                )
                authenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authenticationToken
            }
        }
        filterChain.doFilter(request, response)
    }
}