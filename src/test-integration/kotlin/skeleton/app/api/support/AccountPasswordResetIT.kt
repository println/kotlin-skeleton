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
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.issue.IssueRepository
import skeleton.app.support.access.issue.IssueStatus.*
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.issue.IssueType.*
import skeleton.app.support.access.issue.web.*
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.extensions.ClassExtensions.toObject
import java.time.LocalDateTime
import java.util.*

class AccountPasswordResetIT : AbstractIT() {
    companion object {
        const val RESOURCE = Endpoints.RESET_PASSWORD
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
        return ResetPasswordController(webService)
    }


    private lateinit var account: Account

    @BeforeEach
    fun setup() {
        accountRepository.deleteAll()
        repository.deleteAll()
        account = AccountIT.createAccount(encoder = passwordEncoder, repository = accountRepository)
    }

    @Test
    fun resetPassword() {
        val data = ResetPasswordAccountDto(account.id!!)

        val result = restMockMvc.perform(post(RESOURCE)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andReturn()

        val passwordTemporaryDto: ResetPasswordTemporaryDto = result.response.contentAsString.toObject()

        assertTrue(repository.count() > 0)
        assertNotNull(repository.findFirstByAccountIdAndStatus(account.id!!, OPEN).get())
        account = accountService.authenticate(account.login.username, passwordTemporaryDto.temporaryPassword)
        assertTrue(account.issues == 1)
    }

    @Test
    fun changePassword() {
        val token = IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), FORGOT_PASSWORD)
        val entityToken = repository.save(token)
        val newPassword = "newpassword"
        val data = ResetPasswordDto(entityToken.id!!, newPassword)

        restMockMvc.perform(post("${RESOURCE}/renew", entityToken.id)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isOk)

        account = accountService.authenticate(account.login.username, newPassword)
        assertNotNull(repository.findFirstByAccountIdAndStatus(account.id!!, CLOSED).get())
        assertTrue(account.issues == 0)
    }

    @Nested
    inner class Fails {
        @Test
        fun resetPassword_wrongId() {
            val data = ResetPasswordAccountDto(UUID.randomUUID())

            restMockMvc.perform(post(RESOURCE)
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(data.toJsonString()))
                    .andExpect(status().isBadRequest)
        }

        @Test
        fun changePassword_tokenExpired() {
            val recoveryExpiration =LocalDateTime.now().minusHours(3)
            val token = IssueToken(account.id!!, "1234", recoveryExpiration, TEMPORARY_PASSWORD)
            val entityToken = repository.save(token)
            val newPassword = "newpassword"
            val data = ResetPasswordDto(entityToken.id!!, newPassword)

            restMockMvc.perform(post("${RESOURCE}/renew", entityToken.id)
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