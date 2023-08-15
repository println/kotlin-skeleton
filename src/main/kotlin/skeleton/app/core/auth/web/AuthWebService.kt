package skeleton.app.core.auth.web

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import skeleton.app.core.account.Account
import skeleton.app.core.auth.AuthService
import skeleton.app.core.auth.AuthTokens
import skeleton.app.support.web.AbstractWebService


@Service
class AuthWebService(
        private val authService: AuthService
) : AbstractWebService<Account>() {

    fun register(registerRequest: RegisterRequestDTO): Account {
//        return authService.register(registerRequest)
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Request")
    }

    fun authenticate(authenticationRequest: AuthenticationRequestDTO): Account {
//        val nullableEntity = service.authenticate(authenticationRequest.email, authenticationRequest.password)
//        return assertBadRequest(nullableEntity)
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Request")
    }

    fun generateToken(account: Account): AuthTokens {
//        val token = jwtService.generateToken(account)
//        return AuthTokens(token)
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Request")
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