package skeleton.app.core.user.web

import skeleton.app.core.user.User
import skeleton.app.core.user.UserFilter
import skeleton.app.core.user.UserService
import skeleton.app.support.web.AbstractWebService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class UserWebService(private val service: UserService): AbstractWebService<User>() {
    fun findAll(filter: UserFilter, pageable: Pageable): Page<User> {
        return service.findAll(filter, pageable)
    }

    fun findById(id: UUID): User {
        val nullableEntity = service.findById(id)
        return assertNotFound(nullableEntity)
    }

    fun createOrder(customerId: UUID, pickupAddress: String, deliveryAddress: String): User {
        val nullableEntity = service.createOrder(customerId, pickupAddress, deliveryAddress)
        return assertBadRequest(nullableEntity)
    }

    fun approvePayment(orderId: UUID, value: BigDecimal): User {
        val nullableEntity = service.approvePayment(orderId, value)
        return assertBadRequest(nullableEntity)
    }

    fun refusePayment(orderId: UUID, reason: String): User {
        val nullableEntity = service.refusePayment(orderId, reason)
        return assertBadRequest(nullableEntity)
    }
}