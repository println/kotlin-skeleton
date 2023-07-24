package skeleton.app.domain.auth.web

import skeleton.app.domain.user.Userr
import skeleton.app.domain.user.UserFilter
import skeleton.app.domain.user.UserService
import skeleton.app.support.web.AbstractWebService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class AuthWebService(private val service: UserService): AbstractWebService<Userr>() {
    fun findAll(filter: UserFilter, pageable: Pageable): Page<Userr> {
        return service.findAll(filter, pageable)
    }

    fun findById(id: UUID): Userr {
        val nullableEntity = service.findById(id)
        return assertNotFound(nullableEntity)
    }

    fun createOrder(customerId: UUID, pickupAddress: String, deliveryAddress: String): Userr {
        val nullableEntity = service.createOrder(customerId, pickupAddress, deliveryAddress)
        return assertBadRequest(nullableEntity)
    }

    fun approvePayment(orderId: UUID, value: BigDecimal): Userr {
        val nullableEntity = service.approvePayment(orderId, value)
        return assertBadRequest(nullableEntity)
    }

    fun refusePayment(orderId: UUID, reason: String): Userr {
        val nullableEntity = service.refusePayment(orderId, reason)
        return assertBadRequest(nullableEntity)
    }
}