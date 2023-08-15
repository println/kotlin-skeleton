package skeleton.app.domain.account.web

import skeleton.app.support.web.AbstractWebService
import org.springframework.stereotype.Service
import skeleton.app.domain.account.Account
import skeleton.app.domain.account.AccountService
import skeleton.app.support.jwt.JwtService

@Service
class AuthWebService(
        private val service: AccountService,
        private val jwtService: JwtService
) : AbstractWebService<Account>() {

    fun register(registerRequest: RegisterRequestDTO): Account {
        val nullableEntity = service.register(
                registerRequest.email,
                registerRequest.password,
                registerRequest.user)
        return assertBadRequest(nullableEntity)
    }

    fun authenticate(authenticationRequest: AuthenticationRequestDTO): Account {
        val nullableEntity = service.authenticate(authenticationRequest.email, authenticationRequest.password)
        return assertBadRequest(nullableEntity)
    }

    fun generateToken(account: Account): AuthenticationResponseDTO {
        val token = jwtService.generateToken(account)
        return AuthenticationResponseDTO(token)
    }


}