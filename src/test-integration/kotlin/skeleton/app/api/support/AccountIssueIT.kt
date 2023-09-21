package skeleton.app.api.support

import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.Endpoints.ACCOUNT_ISSUE
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.issue.IssueRepository
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.issue.IssueType
import skeleton.app.support.access.issue.web.ForgotPasswordController
import skeleton.app.support.access.issue.web.ForgotPasswordEmailDto
import skeleton.app.support.access.issue.web.IssueController
import skeleton.app.support.access.issue.web.IssueWebService
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.functions.Generators
import java.util.*

class AccountIssueIT : AbstractWebIT<IssueToken>() {

    companion object {
        const val RESOURCE = ACCOUNT_ISSUE

        fun generateIssue(accountId: UUID = UUID.randomUUID()): IssueToken {
            return IssueToken(
                    accountId = accountId,
                    securityCode = Generators.generateSecurityCode(4),
                    type = IssueType.ACCOUNT_ACTIVATION)
        }
    }

    @Autowired
    private lateinit var repository: IssueRepository

    @Autowired
    private lateinit var service: IssueWebService

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun getRepository() = repository
    override fun getEntityType() = IssueToken::class.java

    override fun getResource() = RESOURCE

    override fun createResource(): Any {
        return IssueController(service)
    }

    private lateinit var account: Account

    @AfterEach
    fun reset() {
        repository.deleteAll()
    }

    @BeforeEach
    fun setup() {
        accountRepository.deleteAll()
        account = AccountIT.createAccount(encoder = passwordEncoder, repository = accountRepository)
    }

    @Nested
    inner class Fails {

        @Test
        fun forgotPassword_idempotent() {
            repository.deleteAll()
            val data = ForgotPasswordEmailDto(account.email)
            val mockMvc = getMvcBuilder(ForgotPasswordController(service)).build()

            val repeatTimes = 10

            repeat(repeatTimes) {
                mockMvc.perform(MockMvcRequestBuilders.post(AccountPasswordForgotIT.RESOURCE)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(data.toJsonString()))
                        .andExpect(status().isCreated)

                account = accountRepository.findById(account.id!!).get()
            }

            Assertions.assertEquals(repeatTimes.toLong(), repository.count())
            Assertions.assertTrue(account.issues == 1)
        }
    }
}

