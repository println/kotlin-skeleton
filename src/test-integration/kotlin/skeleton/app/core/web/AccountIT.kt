package skeleton.app.core.web

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.Endpoints.ACCOUNT
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.account.web.AccountController
import skeleton.app.support.access.account.web.AccountWebService
import skeleton.app.support.access.account.web.UpdateInfoDto
import skeleton.app.support.access.account.web.UpdateLoginDto
import skeleton.app.support.access.login.Login
import skeleton.app.support.extensions.ClassExtensions.toJsonString

class AccountIT : AbstractWebIT<Account>() {

    @Autowired
    private lateinit var repository: AccountRepository

    @Autowired
    private lateinit var webService: AccountWebService

    @Autowired
    private lateinit var service: AccountService

    override fun getRepository() = repository
    override fun getEntityType() = Account::class.java
    override fun preProcessing(data: Account): Account {
        return generateAccount()
    }

    override fun getResource() = ACCOUNT
    override fun createResource(): Any = AccountController(webService)

    @Test
    fun create() {
        val data = generateAccount()

        restMockMvc
                .perform(MockMvcRequestBuilders.post(RESOURCE)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andExpect(header().exists("Location"))
                .andReturn()
    }

    @Test
    fun updateInfo() {
        val entity = entities.first() as Account
        val account = generateAccount()
        val data = UpdateInfoDto(account.firstName, account.lastName)

        restMockMvc
                .perform(put("$RESOURCE/{id}", entity.id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.firstName").value(account.firstName))
                .andExpect(jsonPath("\$.lastName").value(account.lastName))

        val updatedEntity = repository.findById(entity.id!!).get()
        assertNotEquals(entity.firstName, updatedEntity.firstName)
        assertNotEquals(entity.lastName, updatedEntity.lastName)

        assertEquals(data.firstName, updatedEntity.firstName)
        assertEquals(data.lastName, updatedEntity.lastName)
    }

    @Test
    fun updateCredentials() {
        val entity = entities.first() as Account
        val account = generateAccount()
        val data = UpdateLoginDto(account.email, account.login.password)

        restMockMvc
                .perform(put("$RESOURCE/{id}/login", entity.id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.email").value(account.email))

        val updatedEntity = service.authenticate(account.login.username, account.login.password)
        assertNotEquals(entity.email, updatedEntity.email)
        assertNotEquals(entity.login.username, updatedEntity.login.username)
        assertNotEquals(entity.login.password, updatedEntity.login.password)

        assertEquals(data.email, updatedEntity.email)
        assertEquals(data.email, updatedEntity.login.username)
        assertNotEquals(data.password, updatedEntity.login.password)
    }

    companion object {

        private val faker = Faker()
        private val easyRandom = EasyRandom()

        const val RESOURCE = ACCOUNT
        fun createAccount(account: Account = generateAccount(), repository: AccountRepository, encoder: PasswordEncoder): Account {
            val data = Account(
                    account.firstName, account.lastName, account.email,
                    Login(account.login.username,
                            encoder.encode(account.login.password)))
            return repository.save(data)
        }

        fun generateAccount(): Account {
            val firstName = faker.name().firstName()
            val lastName = faker.name().lastName()
            val login = Login(
                    easyRandom.nextObject(String::class.java) + "." + faker.internet().emailAddress(),
                    faker.internet().password(6, 128)
            )
            return Account(firstName, lastName, login.username, login)
        }
    }

}