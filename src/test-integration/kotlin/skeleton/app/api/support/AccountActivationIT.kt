package skeleton.app.api.support

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
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.issue.IssueRepository
import skeleton.app.support.access.issue.IssueStatus.*
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.issue.IssueType.*
import skeleton.app.support.access.issue.web.AccountActivationController
import skeleton.app.support.access.issue.web.IssueWebService
import java.time.LocalDateTime

class AccountActivationIT : AbstractIT() {
    companion object {
        const val RESOURCE = Endpoints.ACCOUNT_ACTIVATION
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
        return AccountActivationController(webService)
    }


    private lateinit var account: Account

    @BeforeEach
    fun setup() {
        accountRepository.deleteAll()
        repository.deleteAll()
        account = AccountIT.createAccount(encoder = passwordEncoder, repository = accountRepository)
    }

    @Test
    fun activeAccount() {
        val token = IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), ACCOUNT_ACTIVATION)
        val entityToken = repository.save(token)

        restMockMvc.perform(get("$RESOURCE/{tokenId}", entityToken.id)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk)

        account = accountRepository.findById(account.id!!).get()

        assertTrue(repository.count() > 0)
        assertNotNull(repository.findFirstByAccountIdAndStatus(account.id!!, CLOSED).get())
        assertTrue(account.issues == 0)
    }

    @Test
    fun forceActiveAccount() {
        val token = IssueToken(account.id!!, "1234", LocalDateTime.now().plusDays(1), ACCOUNT_ACTIVATION)
        repository.save(token)

        restMockMvc.perform(get("$RESOURCE/force/{accountId}", account.id)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk)

        account = accountRepository.findById(account.id!!).get()

        assertTrue(repository.count() > 0)
        assertNotNull(repository.findFirstByAccountIdAndStatus(account.id!!, CLOSED).get())
        assertTrue(account.issues == 0)
    }
}