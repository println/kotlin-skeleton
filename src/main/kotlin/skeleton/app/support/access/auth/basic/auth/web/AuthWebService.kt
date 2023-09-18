package skeleton.app.support.access.auth.basic.auth.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders.*
import org.springframework.http.HttpStatus.*
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.auth.basic.auth.AuthRegisterRequest
import skeleton.app.support.access.auth.basic.auth.AuthRequest
import skeleton.app.support.access.auth.basic.auth.AuthService
import skeleton.app.support.access.auth.basic.auth.AuthTokens
import skeleton.app.support.web.AbstractWebService


@Service
class AuthWebService(
        private val authService: AuthService
) : AbstractWebService() {

    fun register(authRegisterRequest: AuthRegisterRequest): AccountDto {
        val nullableEntity = authService.register(authRegisterRequest)
        return assertBadRequest(nullableEntity)
    }

    fun authenticate(authRequest: AuthRequest): AuthTokens {
        val nullableEntity = authService.authenticate(authRequest)
        return assertBadRequest(nullableEntity)
    }

    fun refreshToken(request: HttpServletRequest,
                     response: HttpServletResponse): AuthTokens {
        val authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw ResponseStatusException(BAD_REQUEST, "Wrong Request")
        }
        val refreshToken = authHeader.substring(7)
        return authService.refreshToken(refreshToken)
    }
}