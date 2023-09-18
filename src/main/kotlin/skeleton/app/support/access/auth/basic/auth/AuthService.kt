package skeleton.app.support.access.auth.basic.auth

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.account.web.AccountRegisterDto
import skeleton.app.support.access.auth.basic.jwt.JwtService
import skeleton.app.support.access.issue.IssueService
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.session.SessionService

@Service
class AuthService(
        private val accountService: AccountService,
        private val issueService: IssueService,
        private val jwtService: JwtService,
        private val sessionService: SessionService) {
    @Transactional
    fun register(authRegisterRequest: AuthRegisterRequest): AccountDto {
        val entityAccount = registerNewAccount(authRegisterRequest)
        createAccountActivationPendency(entityAccount)
        return entityAccount
    }

    @Transactional
    fun authenticate(authRequest: AuthRequest): AuthTokens {
        val account = accountService.authenticate(authRequest.email, authRequest.password)
        val tokens = generateTokens(account)
        revokeOlderSessions(account)
        startNewSession(account, tokens)
        return tokens
    }

    @Transactional
    fun refreshToken(refreshToken: String): AuthTokens {
        val email = jwtService.extractUsername(refreshToken)
        val accountOptional = accountService.findAccountByUsername(email)
        if (accountOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val account = accountOptional.get()
        if (!jwtService.isTokenValid(refreshToken, account)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        }

        val tokens = generateTokens(account)
        revokeOlderSessions(account)
        startNewSession(account, tokens)
        return tokens
    }

    private fun registerNewAccount(authRegisterRequest: AuthRegisterRequest): AccountDto {
        return accountService.register(AccountRegisterDto(
                authRegisterRequest.firstName,
                authRegisterRequest.lastName,
                authRegisterRequest.email,
                authRegisterRequest.password))!!
    }

    private fun createAccountActivationPendency(account: AccountDto): IssueToken?{
        return issueService.createPendencyOfAccountActivation(account.id!!)
    }

    private fun generateTokens(account: Account): AuthTokens {
        return AuthTokens(
                accessToken = jwtService.generateToken(account),
                refreshToken = jwtService.generateRefreshToken(account))
    }

    private fun startNewSession(account: Account, authTokens: AuthTokens) {
        sessionService.start(account, authTokens.accessToken)
    }

    private fun revokeOlderSessions(account: Account) {
        sessionService.revokeAllSessionsByAccount(account.id!!)
    }
}