package skeleton.app.domain.web

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.ResourcePaths
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserRepository
import skeleton.app.domain.user.UserStatus
import skeleton.app.domain.user.web.UserController
import skeleton.app.domain.user.web.UserDto
import skeleton.app.domain.user.web.UserWebService
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import java.util.*

class UserApiTests : AbstractWebIT<User>() {

    companion object {
        const val RESOURCE = ResourcePaths.USER
    }

    @Autowired
    private lateinit var repository: UserRepository

    @Autowired
    private lateinit var service: UserWebService


    override fun getRepository() = repository
    override fun getEntityType() = User::class.java
    override fun preProcessing(data: User) {
        data.status = UserStatus.WAITING_PAYMENT
    }

    override fun getResource() = RESOURCE

    override fun createResource(): Any {
        return UserController(service)
    }

    @AfterEach
    fun reset() {
        repository.deleteAll()
    }

    @Test
    fun create() {
        val customerId = UUID.randomUUID()
        val pickupAddress = "pickupAddress"
        val destination = "destination"

        val data = UserDto(customerId, pickupAddress, destination)

        restMockMvc
                .perform(post(RESOURCE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andExpect(header().exists("Location"))
                .andReturn()
    }


}
