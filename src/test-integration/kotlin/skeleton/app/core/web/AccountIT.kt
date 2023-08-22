package skeleton.app.core.web

import com.github.javafaker.Faker
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.ResourcePaths
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRepository
import skeleton.app.support.access.account.web.*
import skeleton.app.support.access.auth.basic.login.Login
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.extensions.ClassExtensions.toObject

class AccountIT : AbstractWebIT<Account>() {

    @Autowired
    private lateinit var repository: AccountRepository

    @Autowired
    private lateinit var service: AccountWebService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    override fun getRepository() = repository
    override fun getEntityType() = Account::class.java
    override fun preProcessing(data: Account): Account {
        return generateAccount()
    }
    override fun getResource() = RESOURCE
    override fun createResource(): Any = AccountController(service)

    @Test
    fun create() {
        val data = generateAccount()

        restMockMvc
                .perform(MockMvcRequestBuilders.post(RESOURCE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andReturn()
    }

    @Test
    fun updateInfo() {
        val entity = entities.first() as Account
        val account = generateAccount()
        val data = UpdateInfoDto(account.firstName, account.lastName)

        restMockMvc
                .perform(MockMvcRequestBuilders.put("$RESOURCE/{id}", entity.id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.firstName").value(account.firstName))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.lastName").value(account.lastName))

        val updatedEntity = repository.findById(entity.id!!).get()
        Assertions.assertNotEquals(entity.firstName, updatedEntity.firstName)
        Assertions.assertNotEquals(entity.lastName, updatedEntity.lastName)

        Assertions.assertEquals(data.firstName, updatedEntity.firstName)
        Assertions.assertEquals(data.lastName, updatedEntity.lastName)
    }

    @Test
    fun updateCredentials() {
        val entity = entities.first() as Account
        val account = generateAccount()
        val data = UpdateLoginDto(account.email, account.login.password)

        restMockMvc
                .perform(MockMvcRequestBuilders.put("$RESOURCE/{id}/login", entity.id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.email").value(account.email))

        val updatedEntity = repository.findById(entity.id!!).get()
        Assertions.assertNotEquals(entity.email, updatedEntity.email)
        Assertions.assertNotEquals(entity.login.username, updatedEntity.login.username)
        Assertions.assertNotEquals(entity.login.password, updatedEntity.login.password)

        Assertions.assertEquals(data.email, updatedEntity.email)
        Assertions.assertEquals(data.email, updatedEntity.login.username)
        Assertions.assertNotEquals(data.password, updatedEntity.login.password)
    }

    companion object {

        private val faker = Faker()
        private val easyRandom = EasyRandom()

        const val RESOURCE = ResourcePaths.ACCOUNT
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