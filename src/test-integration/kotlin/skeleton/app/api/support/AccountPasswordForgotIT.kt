package skeleton.app.api.support

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
import skeleton.app.support.access.issue.IssueStatus.*
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.issue.IssueType
import skeleton.app.support.access.issue.web.*
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.extensions.ClassExtensions.toObject
import java.time.LocalDateTime

class AccountPasswordForgotIT : AbstractIT() {

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

        account = accountRepository.findById(account.id!!).get()

        assertTrue(repository.count() > 0)
        assertNotNull(repository.findFirstByAccountIdAndStatus(account.id!!, OPEN).get())
        assertTrue(account.issues == 1)
    }

    @Test
    fun changePassword() {
        val token = IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), IssueType.FORGOT_PASSWORD)
        val entityToken = repository.save(token)
        val newPassword = "newpassword"
        val data = ForgotPasswordDto(entityToken.id!!, newPassword)

        restMockMvc.perform(post("$RESOURCE/renew")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isOk)

        account = accountService.authenticate(account.login.username, newPassword)
        assertNotNull(repository.findFirstByAccountIdAndStatus(account.id!!, CLOSED).get())
        assertTrue(account.issues == 0)
    }

    @Test
    fun changePasswordCloseTemporaryPassword() {
        val tokenTemporaryPassword = IssueToken(account.id!!, "4321", LocalDateTime.now().plusDays(1), IssueType.TEMPORARY_PASSWORD)
        val token = IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), IssueType.FORGOT_PASSWORD)
        val entityToken = repository.save(token)
        val entityTokenTemporaryPassword = repository.save(tokenTemporaryPassword)
        val newPassword = "newpassword"
        val data = ForgotPasswordDto(entityToken.id!!, newPassword)

        assertEquals(2, repository.findAllByAccountIdAndStatus(account.id!!, OPEN).size)

        restMockMvc.perform(post("$RESOURCE/renew")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isOk)

        account = accountService.authenticate(account.login.username, newPassword)
        assertNotNull(repository.findFirstByAccountIdAndStatus(account.id!!, CLOSED).get())
        assertEquals(CLOSED, repository.findById(entityTokenTemporaryPassword.id!!).get().status)
        assertEquals(0, account.issues)
    }

    @Test
    fun findTokenBySecurityCode() {
        val token = IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), IssueType.FORGOT_PASSWORD)
        repository.save(token)

        val result = restMockMvc.perform(get("$RESOURCE/code/{code}", token.securityCode)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()

        val tokenDto: ForgotPasswordTokenDto = result.response.contentAsString.toObject()
        assertEquals(token.id!!, tokenDto.token)
    }

    @Nested
    inner class Fails {

        @Test
        fun findTokenBySecurityCode_tokenExpired() {
            val recoveryExpiration = LocalDateTime.now().minusHours(3)
            val token = IssueToken(account.id!!, "1234", recoveryExpiration, IssueType.FORGOT_PASSWORD)
            repository.save(token)

            restMockMvc.perform(get("$RESOURCE/code/{code}", token.securityCode)
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isBadRequest)
        }

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
            val recoveryExpiration = LocalDateTime.now().minusHours(3)
            val token = IssueToken(account.id!!, "1234", recoveryExpiration, IssueType.FORGOT_PASSWORD)
            val entityToken = repository.save(token)
            val newPassword = "newpassword"
            val data = ForgotPasswordDto(entityToken.id!!, newPassword)

            restMockMvc.perform(post("$RESOURCE/renew")
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
            val entityToken = repository.save(token)
            val newPassword = "newpassword"
            val data = ForgotPasswordDto(entityToken.id!!, newPassword)

            restMockMvc.perform(post("$RESOURCE/renew")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(data.toJsonString()))
                    .andExpect(status().isBadRequest)

            assertThrows<BadCredentialsException> {
                accountService.authenticate(account.login.username, newPassword)
            }
        }

        @Test
        fun changePassword_wrongSecurityCodeFromAnotherIssue() {
            val token = IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), IssueType.ACCOUNT_ACTIVATION)
            repository.save(token)

            restMockMvc.perform(get("$RESOURCE/code/{code}", token.securityCode)
                    .accept(APPLICATION_JSON))
                    .andExpect(status().isBadRequest)
        }
    }
}