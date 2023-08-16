package skeleton.app.core.web

import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import skeleton.app.AbstractIT
import skeleton.app.configuration.constants.ResourcePaths
import skeleton.app.core.account.Account
import skeleton.app.core.account.AccountRepository
import skeleton.app.core.auth.web.AuthController
import skeleton.app.core.auth.web.AuthWebService
import skeleton.app.core.token.TokenRepository
import skeleton.app.core.user.User
import skeleton.app.core.user.UserRepository
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import skeleton.app.core.auth.AuthTokens
import skeleton.app.core.token.Token
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.extensions.ClassExtensions.toObject

class AuthApiIT : AbstractIT() {

    val easyRandom = EasyRandom()

    companion object {
        const val RESOURCE = ResourcePaths.USER
    }

    @Autowired
    private lateinit var repository: UserRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var tokenRepository: TokenRepository

    @Autowired
    private lateinit var authWebService: AuthWebService

    override fun createResource(): Any {
        return AuthController(authWebService)
    }

    @BeforeEach
    fun reset() {
        tokenRepository.deleteAll()
        repository.deleteAll()
        accountRepository.deleteAll()
    }

    @Test
    fun register() {
        val email = "email@com.br"
        val password = "password"
        val user = easyRandom.nextObject(User::class.java)

        val data = Account(email, password, user)

        val result = restMockMvc.perform(post("/api/v1/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andReturn()

        val authTokens: AuthTokens = result.response.contentAsString.toObject()
        Assertions.assertNotNull(authTokens.accessToken)
        Assertions.assertNotNull(authTokens.refreshToken)

        val tokens: List<Token> = tokenRepository.findAll()
        Assertions.assertTrue(tokens.size == 1)

        val accounts: List<Account> = accountRepository.findAll()
        Assertions.assertTrue(accounts.size == 1)

        val users: List<User> = repository.findAll()
        Assertions.assertTrue(accounts.size == 1)

    }
}