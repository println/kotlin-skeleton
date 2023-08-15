package skeleton.app.core.auth

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import skeleton.app.core.account.Account
import skeleton.app.core.account.AccountService
import skeleton.app.core.auth.jwt.JwtService
import skeleton.app.core.auth.web.AuthenticationRequestDTO
import skeleton.app.core.auth.web.RegisterRequestDTO
import skeleton.app.core.token.TokenService
import java.lang.RuntimeException
import java.util.*

@Service
class AuthService(
        private val accountService: AccountService,
        private val jwtService: JwtService,
        private val tokenService: TokenService) {
    @Transactional
    fun register(registerRequest: RegisterRequestDTO): AuthTokens {
        val account = registerNewAccount(registerRequest)
        val tokens = generateTokens(account)
        storeAccountToken(account, tokens)
        return tokens
    }

    @Transactional
    fun authenticate(authenticationRequest: AuthenticationRequestDTO): AuthTokens {
        val accountOptional = authenticateAccount(authenticationRequest)
        if (accountOptional.isEmpty) {
            throw RuntimeException("bad request")
        }

        val account = accountOptional.get()
        val tokens = generateTokens(account)
        revokeAllTokens(account)
        storeAccountToken(account, tokens)
        return tokens
    }

    @Transactional
    fun refreshToken(refreshToken: String): AuthTokens {
        val accountEmail = jwtService.extractUsername(refreshToken)
        val accountOptional = findAccountByEmail(accountEmail)
        if (accountOptional.isEmpty) {
            throw RuntimeException("bad request")
        }

        val account = accountOptional.get()
        if(!jwtService.isTokenValid(refreshToken, account)){
            throw RuntimeException("bad request")
        }

        val tokens = generateTokens(account)
        revokeAllTokens(account)
        storeAccountToken(account, tokens)
        return tokens
    }
    private fun registerNewAccount(registerRequest: RegisterRequestDTO): Account {
        return accountService.register(registerRequest.email, registerRequest.password, registerRequest.user)!!
    }


    private fun authenticateAccount(authenticationRequest: AuthenticationRequestDTO): Optional<Account> {
        return accountService.authenticate(authenticationRequest.email, authenticationRequest.password)
    }

    private fun findAccountByEmail(email: String): Optional<Account>{
        return accountService.findById(email)
    }

    private fun generateTokens(account: Account): AuthTokens {
        return AuthTokens(
                accessToken = jwtService.generateToken(account),
                refreshToken = jwtService.generateRefreshToken(account))
    }

    private fun storeAccountToken(account: Account, authTokens: AuthTokens) {
        tokenService.add(account, authTokens.accessToken)
    }

    private fun revokeAllTokens(account: Account) {
        tokenService.revokeAllUserTokens(account)
    }
}