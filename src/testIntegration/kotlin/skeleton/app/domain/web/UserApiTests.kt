package skeleton.app.domain.web

import skeleton.app.AbstractWebTest
import skeleton.app.configuration.constants.ResourcePaths
import skeleton.app.support.eventsourcing.connectors.dummy.DummyProducerConnector
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import skeleton.app.support.extensions.ClassExtensions.toObject
import gsl.schemas.OrderEvent
import gsl.schemas.OrderEventStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import skeleton.app.domain.user.*
import skeleton.app.domain.user.web.*
import java.util.*

class UserApiTests : AbstractWebTest<User>() {

    companion object {
        const val RESOURCE = ResourcePaths.ORDER
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
    fun createOrder() {
        val customerId = UUID.randomUUID()
        val pickupAddress = "pickupAddress"
        val destination = "destination"

        val data = UserDto(customerId, pickupAddress, destination)

        val result = restMockMvc
                .perform(post(RESOURCE)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data.toJsonString()))
                .andExpect(status().isCreated)
                .andExpect(header().exists("Location"))
                .andReturn()

        val order: User = result.response.contentAsString.toObject()

        assertTotalMessagesAndReleaseThem(2)

        val eventContent = DummyProducerConnector.getMessageContent(OrderEvent::class)
        Assertions.assertEquals(OrderEventStatus.WAITING_PAYMENT, eventContent?.status)
        Assertions.assertEquals(order.id, eventContent?.trackId)

        assertDocumentReleased(order)
    }


}
