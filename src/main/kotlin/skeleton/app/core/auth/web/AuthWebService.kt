package skeleton.app.core.auth.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import skeleton.app.core.account.Account
import skeleton.app.core.auth.AuthService
import skeleton.app.core.auth.AuthTokens
import skeleton.app.core.auth.AuthRequest
import skeleton.app.core.auth.AuthRegisterRequest
import skeleton.app.core.token.Token
import skeleton.app.support.web.AbstractWebService


@Service
class AuthWebService(
        private val authService: AuthService
) : AbstractWebService<AuthTokens>() {

    fun register(authRegisterRequest: AuthRegisterRequest): AuthTokens {
        val nullableEntity = authService.register(authRegisterRequest)
        return assertBadRequest(nullableEntity)
    }

    fun authenticate(authRequest: AuthRequest): AuthTokens {
        val nullableEntity = authService.authenticate(authRequest)
        return assertBadRequest(nullableEntity)
    }

    fun refreshToken(request: HttpServletRequest,
                     response: HttpServletResponse): AuthTokens {
        val authHeader = request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Request")
        }
        val refreshToken = authHeader.substring(7)
        return authService.refreshToken(refreshToken)
    }
}