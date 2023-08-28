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
import skeleton.app.configuration.constants.Endpoints.RECOVERY
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.auth.basic.recovery.RecoveryRepository
import skeleton.app.support.access.auth.basic.recovery.RecoveryToken
import skeleton.app.support.access.auth.basic.recovery.web.RecoveryController
import skeleton.app.support.access.auth.basic.recovery.web.RecoveryEmailDto
import skeleton.app.support.access.auth.basic.recovery.web.RecoveryPasswordDto
import skeleton.app.support.access.auth.basic.recovery.web.RecoveryWebService
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import java.time.Duration
import java.time.Instant
import java.util.*

class RecoveryPasswordIT : AbstractIT() {

    companion object {
        const val RESOURCE = RECOVERY
    }

    @Autowired
    private lateinit var repository: RecoveryRepository

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var webService: RecoveryWebService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun createResource(): Any {
        return RecoveryController(webService)
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
        val data = RecoveryEmailDto(account.email)

        restMockMvc.perform(post("${RESOURCE}/forgot")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(data.toJsonString()))
                .andExpect(status().isCreated)

        assertTrue(repository.count() > 0)
        assertTrue(repository.existsByAccountId(account.id!!))
    }

    @Test
    fun changePassword() {
        val entity = repository.save(RecoveryToken(account.id!!, "1234"))
        val newPassword = "newpassword"
        val data = RecoveryPasswordDto(newPassword, entity.securityCode)

        restMockMvc.perform(post("${RESOURCE}/change-password/{token}", entity.id)
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
            val data = RecoveryEmailDto("xpto@xpto.com")

            restMockMvc.perform(post("${RESOURCE}/forgot")
                    .accept(APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .content(data.toJsonString()))
                    .andExpect(status().isCreated)

            assertFalse(repository.count() > 0)
            assertFalse(repository.existsByAccountId(account.id!!))
        }

        @Test
        fun changePassword_tokenExpired() {
            val recoveryDate = Date.from(Instant.now().minus(Duration.ofHours(3)))
            val token = RecoveryToken(account.id!!, "1234", recoveryDate)
            val entity = repository.save(token)
            val newPassword = "newpassword"
            val data = RecoveryPasswordDto(newPassword, entity.securityCode)

            restMockMvc.perform(post("${RESOURCE}/change-password/{token}", entity.id)
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
            val token = RecoveryToken(account.id!!, "1234")
            val entity = repository.save(token)
            val newPassword = "newpassword"
            val data = RecoveryPasswordDto(newPassword, "5678")

            restMockMvc.perform(post("${RESOURCE}/change-password/{token}", entity.id)
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