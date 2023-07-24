package skeleton.app.domain.eventsourcing

import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import skeleton.app.AbstractEventSourcingTest
import skeleton.app.configuration.constants.ResourcePaths
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserRepository
import skeleton.app.domain.user.UserStatus
import skeleton.app.domain.user.web.UserController
import skeleton.app.domain.user.web.UserWebService
import java.util.*

class UserCommandTest : AbstractEventSourcingTest() {

    companion object {
        const val RESOURCE = ResourcePaths.ORDER
    }

    @Autowired
    private lateinit var repository: UserRepository

    @Autowired
    private lateinit var service: UserWebService


    override fun createResource(): Any {
        return UserController(service)
    }

    val easyRandom = EasyRandom()
    var entity: User = easyRandom.nextObject(User::class.java)

    @AfterEach
    fun reset() {
        repository.deleteAll()
    }

    @BeforeEach
    fun setupEntity() {
        val customerId = UUID.randomUUID()
        val pickupAddress = "pickupAddress"
        val destination = "destination"
        val data = User(customerId, pickupAddress, destination)
        entity = repository.saveAndFlush(data)
    }

    @Test
    fun approved() {
        val order = checkAllFromApiAndGetFirst<User>(RESOURCE)

        Assertions.assertEquals(UserStatus.ACCEPTED, order.status)

        assertTotalMessagesAndReleaseThem(3)

        assertDocumentReleased(order)
    }

    @Test
    fun refused() {
        val order = checkAllFromApiAndGetFirst<User>(RESOURCE)

        Assertions.assertEquals(UserStatus.REFUSED, order.status)

        assertTotalMessagesAndReleaseThem(2)

        assertDocumentReleased(order)
    }

    @Test
    fun checkApprovedDuplication() {
        val totalMessages = 3L

        checkAllFromApiAndGetFirst<User>(RESOURCE)
        assertTotalMessagesAndReleaseThem(totalMessages)
    }

    @Test
    fun checkRefusedDuplication() {
        val totalMessages = 2L

        checkAllFromApiAndGetFirst<User>(RESOURCE)
        assertTotalMessagesAndReleaseThem(totalMessages)
    }

}