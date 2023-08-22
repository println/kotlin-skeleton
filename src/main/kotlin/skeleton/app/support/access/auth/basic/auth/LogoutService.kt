package skeleton.app.support.access.auth.basic.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
import skeleton.app.support.access.session.SessionService

@Service
class LogoutService(
        private val sessionService: SessionService
) : LogoutHandler {
    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        val authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return
        }

        val token = authHeader.substring(7);
        val entityTokenOptional = sessionService.findByToken(token)

        if (entityTokenOptional.isPresent) {
            val entityToken = entityTokenOptional.get()
            entityToken.expired = true
            entityToken.revoked = true
            sessionService.save(entityToken);
            SecurityContextHolder.clearContext();
        }
    }
}