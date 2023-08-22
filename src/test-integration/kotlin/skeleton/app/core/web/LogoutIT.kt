package skeleton.app.core.web

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import skeleton.app.IntegrationTest
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.auth.basic.auth.AuthRequest
import skeleton.app.support.access.auth.basic.auth.AuthTokens
import skeleton.app.support.access.session.SessionRepository
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.extensions.ClassExtensions.toObject


class LogoutIT : IntegrationTest() {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var sessionRepository: SessionRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup(){
        sessionRepository.deleteAll()
        accountRepository.deleteAll()

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply<DefaultMockMvcBuilder>(springSecurity())
                .build()
    }
    @Test
    fun logout() {
        val account = AccountIT.generateAccount()
        val entityAccount = AccountIT.createAccount(account, accountRepository, passwordEncoder)
        val credentials = AuthRequest(account.login.username, account.login.password)

        val tokens: AuthTokens = mockMvc.perform(MockMvcRequestBuilders.post("${AuthApiIT.RESOURCE}/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credentials.toJsonString()))
                .andReturn().response.contentAsString.toObject()

        Assertions.assertTrue(sessionRepository.existsByAccountIdAndExpiredIsFalseAndRevokedIsFalse(entityAccount.id))

        mockMvc.perform(MockMvcRequestBuilders.post("${AuthApiIT.RESOURCE}/logout")
                .secure(true)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .header("Authorization", "Bearer ${tokens.accessToken}"))
                .andExpect(MockMvcResultMatchers.status().isOk)

        Assertions.assertFalse(sessionRepository.existsByAccountIdAndExpiredIsFalseAndRevokedIsFalse(entityAccount.id))
    }
}