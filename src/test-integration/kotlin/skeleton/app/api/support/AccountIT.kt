package skeleton.app.api.support

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.Endpoints.ACCOUNT
import skeleton.app.support.access.account.*
import skeleton.app.support.access.account.web.*
import skeleton.app.support.access.login.Login
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.functions.Functions

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
        val data = generateAccountRegister()

        restMockMvc
                .perform(post(RESOURCE)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andExpect(header().exists("Location"))
                .andReturn()
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

    @Test
    fun updateRole(){
        val entity = entities.first() as Account
        val data = RoleDto(AccountRole.ADMIN)

        restMockMvc
                .perform(put("$RESOURCE/{id}/role", entity.id)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.role").value(data.role.name))
    }

    @Test
    fun block(){
        val entity = entities.first() as Account

        restMockMvc
                .perform(put("$RESOURCE/{id}/block", entity.id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.isEnabled").value(false))
    }
    @Test
    fun unblock(){
        val account  = generateAccount()
        account.status = AccountStatus.BLOCKED
        val entity = repository.save(account)

        restMockMvc
                .perform(delete("$RESOURCE/{id}/block", entity.id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$.isEnabled").value(true))
    }

    companion object {

        private val faker = Faker()
        private val easyRandom = EasyRandom()

        const val RESOURCE = ACCOUNT
        fun createAccount(account: Account = generateAccount(), repository: AccountRepository, encoder: PasswordEncoder): Account {
            val data = Account(
                    account.name,
                    account.email,
                    Login(account.login.username,
                            encoder.encode(account.login.password)))
            return repository.save(data)
        }

        fun generateAccount(): Account {
            val name = faker.name().name()
            val login = Login(
                    Functions.Text.cleaner(easyRandom.nextObject(String::class.java) + "." + faker.internet().emailAddress()),
                    faker.internet().password(6, 128)
            )
            return Account(name, login.username, login)
        }

        fun generateAccountRegister(): AccountRegisterDto {
            val account = generateAccount()
            val firstName = faker.name().firstName()
            val lastName = faker.name().lastName()
            return AccountRegisterDto(firstName, lastName, account.email, account.password)
        }
    }

}