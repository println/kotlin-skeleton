package skeleton.app.core.web

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import skeleton.app.AbstractIT
import skeleton.app.configuration.constants.Endpoints.FORGOT_PASSWORD
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.issue.IssueRepository
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.issue.IssueType
import skeleton.app.support.access.issue.web.ForgotPasswordController
import skeleton.app.support.access.issue.web.ForgotPasswordDto
import skeleton.app.support.access.issue.web.ForgotPasswordEmailDto
import skeleton.app.support.access.issue.web.IssueWebService
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import java.time.LocalDateTime

class RecoveryPasswordIT : AbstractIT() {

    companion object {
        const val RESOURCE = FORGOT_PASSWORD
    }

    @Autowired
    private lateinit var repository: IssueRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var webService: IssueWebService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun createResource(): Any {
        return ForgotPasswordController(webService)
    }


    private lateinit var account: Account

    @BeforeEach
    fun setup() {
        accountRepository.deleteAll()
        repository.deleteAll()
        account = AccountIT.createAccount(encoder = passwordEncoder, repository = accountRepository)
    }

    @Test
    fun forgotPassword() {
        val data = ForgotPasswordEmailDto(account.email)

        restMockMvc.perform(post(RESOURCE)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isCreated)

        assertTrue(repository.count() > 0)
        assertTrue(repository.existsByAccountId(account.id!!))
    }

    @Test
    fun changePassword() {
        val entity = repository.save(IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), IssueType.FORGOT_PASSWORD))
        val newPassword = "newpassword"
        val data = ForgotPasswordDto(newPassword, entity.securityCode)

        restMockMvc.perform(post("${RESOURCE}/renew/{token}", entity.id)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isOk)

        accountService.authenticate(account.login.username, newPassword)
    }

    @Nested
    inner class Fails {

        @Test
        fun forgotPassword_wrongEmail() {
            val data = ForgotPasswordEmailDto("xpto@xpto.com")

            restMockMvc.perform(post(RESOURCE)
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(data.toJsonString()))
                    .andExpect(status().isCreated)

            assertFalse(repository.count() > 0)
            assertFalse(repository.existsByAccountId(account.id!!))
        }

        @Test
        fun changePassword_tokenExpired() {
            val recoveryExpiration =LocalDateTime.now().minusHours(3)
            val token = IssueToken(account.id!!, "1234", recoveryExpiration, IssueType.FORGOT_PASSWORD)
            val entity = repository.save(token)
            val newPassword = "newpassword"
            val data = ForgotPasswordDto(newPassword, entity.securityCode)

            restMockMvc.perform(post("${RESOURCE}/renew/{token}", entity.id)
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(data.toJsonString()))
                    .andExpect(status().isBadRequest)

            assertThrows<BadCredentialsException> {
                accountService.authenticate(account.login.username, newPassword)
            }
        }

        @Test
        fun changePassword_wrongSecurityCode() {
            val token = IssueToken(account.id!!, "1234", type = IssueType.FORGOT_PASSWORD)
            val entity = repository.save(token)
            val newPassword = "newpassword"
            val data = ForgotPasswordDto(newPassword, "5678")

            restMockMvc.perform(post("${RESOURCE}/renew/{token}", entity.id)
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(data.toJsonString()))
                    .andExpect(status().isBadRequest)

            assertThrows<BadCredentialsException> {
                accountService.authenticate(account.login.username, newPassword)
            }
        }
    }
}