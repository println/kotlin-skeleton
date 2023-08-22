package skeleton.app.core.web

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import skeleton.app.AbstractIT
import skeleton.app.configuration.constants.ResourcePaths
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserRepository
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.auth.basic.auth.AuthRequest
import skeleton.app.support.access.auth.basic.auth.AuthTokens
import skeleton.app.support.access.auth.basic.auth.web.AuthController
import skeleton.app.support.access.auth.basic.auth.web.AuthWebService
import skeleton.app.support.access.session.Session
import skeleton.app.support.access.session.SessionRepository
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.extensions.ClassExtensions.toObject


class AuthApiIT : AbstractIT() {


    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var sessionRepository: SessionRepository

    @Autowired
    private lateinit var authWebService: AuthWebService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun createResource(): Any {
        return AuthController(authWebService)
    }

    @BeforeEach
    fun reset() {
        sessionRepository.deleteAll()
        accountRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun register() {
        val data = AccountIT.generateAccount()

        val result = restMockMvc.perform(post("$RESOURCE/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andReturn()

        val authTokens: AuthTokens = result.response.contentAsString.toObject()
        Assertions.assertNotNull(authTokens.accessToken)
        Assertions.assertNotNull(authTokens.refreshToken)

        val sessions: List<Session> = sessionRepository.findAll()
        Assertions.assertTrue(sessions.size == 1)

        val accounts: List<Account> = accountRepository.findAll()
        Assertions.assertTrue(accounts.size == 1)

        val users: List<User> = userRepository.findAll()
        Assertions.assertTrue(users.isEmpty())

    }

    @Test
    fun authenticate() {
        val account = AccountIT.generateAccount()
        val credentials = AuthRequest(account.login.username, account.login.password)

        AccountIT.createAccount(account, accountRepository, passwordEncoder)

        val result = restMockMvc.perform(post("$RESOURCE/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials.toJsonString()))
                .andExpect(status().isOk)
                .andReturn()

        val authTokens: AuthTokens = result.response.contentAsString.toObject()
        Assertions.assertNotNull(authTokens.accessToken)
        Assertions.assertNotNull(authTokens.refreshToken)

        val sessions: List<Session> = sessionRepository.findAll()
        Assertions.assertTrue(sessions.size == 1)
    }

    companion object {
        const val RESOURCE = ResourcePaths.AUTH
    }

}