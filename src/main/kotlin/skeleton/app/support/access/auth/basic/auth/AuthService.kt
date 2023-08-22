package skeleton.app.support.access.auth.basic.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.account.web.AccountRegisterDto
import skeleton.app.support.access.auth.basic.jwt.JwtService
import skeleton.app.support.access.session.SessionService
import java.util.*

@Service
class AuthService(
        private val accountService: AccountService,
        private val jwtService: JwtService,
        private val sessionService: SessionService) {
    @Transactional
    fun register(authRegisterRequest: AuthRegisterRequest): AuthTokens {
        val account = registerNewAccount(authRegisterRequest)
        val tokens = generateTokens(account)
        storeAccountToken(account, tokens)
        return tokens
    }

    @Transactional
    fun authenticate(authRequest: AuthRequest): AuthTokens {
        val accountOptional = authenticateAccount(authRequest)
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
        if (!jwtService.isTokenValid(refreshToken, account)) {
            throw RuntimeException("bad request")
        }

        val tokens = generateTokens(account)
        revokeAllTokens(account)
        storeAccountToken(account, tokens)
        return tokens
    }



    private fun registerNewAccount(authRegisterRequest: AuthRegisterRequest): Account {
        return accountService.register(AccountRegisterDto(
                authRegisterRequest.firstName,
                authRegisterRequest.lastName,
                authRegisterRequest.email,
                authRegisterRequest.password))!!
    }


    private fun authenticateAccount(authRequest: AuthRequest): Optional<Account?> {
        return accountService.authenticate(authRequest.email, authRequest.password)
    }

    private fun findAccountByEmail(email: String): Optional<Account?> {
        return accountService.findByEmail(email)
    }

    private fun generateTokens(account: Account): AuthTokens {
        return AuthTokens(
                accessToken = jwtService.generateToken(account),
                refreshToken = jwtService.generateRefreshToken(account))
    }

    private fun storeAccountToken(account: Account, authTokens: AuthTokens) {
        sessionService.add(account, authTokens.accessToken)
    }

    private fun revokeAllTokens(account: Account) {
        sessionService.revokeAllUserTokens(account)
    }
}