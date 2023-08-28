package skeleton.app.core.web

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import skeleton.app.AbstractIT
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.core.web.AccountIT.Companion.generateAccount
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserRepository
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.account.AccountService
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
    private lateinit var webService: AuthWebService

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun createResource(): Any {
        return AuthController(webService)
    }

    @BeforeEach
    fun reset() {
        sessionRepository.deleteAll()
        accountRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun register() {
        val data = generateAccount()

        val result = restMockMvc.perform(post("$RESOURCE/register")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andReturn()

        val authTokens: AuthTokens = result.response.contentAsString.toObject()
        assertNotNull(authTokens.accessToken)
        assertNotNull(authTokens.refreshToken)

        val entitySessions: List<Session> = sessionRepository.findAll()
        assertTrue(entitySessions.size == 1)

        accountService.authenticate(data.login.username, data.login.password)
        
        val entityUsers: List<User> = userRepository.findAll()
        assertTrue(entityUsers.isEmpty())

    }

    @Test
    fun authenticate() {
        val account = generateAccount()
        val credentials = AuthRequest(account.login.username, account.login.password)

        AccountIT.createAccount(account, accountRepository, passwordEncoder)

        val result = restMockMvc.perform(post("$RESOURCE/authenticate")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(credentials.toJsonString()))
                .andExpect(status().isOk)
                .andReturn()

        val authTokens: AuthTokens = result.response.contentAsString.toObject()
        assertNotNull(authTokens.accessToken)
        assertNotNull(authTokens.refreshToken)

        val entitySessions: List<Session> = sessionRepository.findAll()
        assertTrue(entitySessions.size == 1)
    }

    companion object {
        const val RESOURCE = Endpoints.AUTH
    }
}