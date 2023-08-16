package skeleton.app.core.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
import skeleton.app.core.token.TokenService

@Service
class LogoutService(
        private val tokenService: TokenService
): LogoutHandler {
    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        val authHeader = request.getHeader("Authorization");

        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return
        }

        val token = authHeader.substring(7);
        val entityTokenOptional = tokenService.findByToken(token)

        if (entityTokenOptional.isPresent) {
            val entityToken = entityTokenOptional.get()
            entityToken.expired = true
            entityToken.revoked = true
            tokenService.save(entityToken);
            SecurityContextHolder.clearContext();
        }
    }
}