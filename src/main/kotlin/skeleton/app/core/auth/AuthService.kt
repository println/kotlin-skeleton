package skeleton.app.core.auth

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import skeleton.app.core.account.Account
import skeleton.app.core.account.AccountService
import skeleton.app.core.auth.jwt.JwtService
import skeleton.app.core.token.TokenService
import java.util.*

@Service
class AuthService(
        private val accountService: AccountService,
        private val jwtService: JwtService,
        private val tokenService: TokenService) {
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
        if(!jwtService.isTokenValid(refreshToken, account)){
            throw RuntimeException("bad request")
        }

        val tokens = generateTokens(account)
        revokeAllTokens(account)
        storeAccountToken(account, tokens)
        return tokens
    }
    private fun registerNewAccount(authRegisterRequest: AuthRegisterRequest): Account {
        return accountService.register(authRegisterRequest.email, authRegisterRequest.password, authRegisterRequest.user)!!
    }


    private fun authenticateAccount(authRequest: AuthRequest): Optional<Account?> {
        return accountService.authenticate(authRequest.email, authRequest.password)
    }

    private fun findAccountByEmail(email: String): Optional<Account?>{
        return accountService.findByEmail(email)
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