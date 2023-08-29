package skeleton.app.domain.user.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserFilter
import skeleton.app.domain.user.UserService
import skeleton.app.support.web.AbstractWebService
import java.math.BigDecimal
import java.util.*

@Service
class UserWebService(private val service: UserService): AbstractWebService() {
    fun findAll(filter: UserFilter, pageable: Pageable): Page<User> {
        return service.findAll(filter, pageable)
    }

    fun findById(id: UUID): User {
        val nullableEntity = service.findById(id)
        return assertNotFound(nullableEntity)
    }

    fun createOrder(customerId: UUID, pickupAddress: String, deliveryAddress: String): User {
        //val nullableEntity = service.create(customerId, pickupAddress, deliveryAddress)
        return assertBadRequest(null)
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