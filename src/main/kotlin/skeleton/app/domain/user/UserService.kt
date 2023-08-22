package skeleton.app.domain.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class UserService(
        private val repository: UserRepository) {

    fun findAll(userFilter: UserFilter, pageable: Pageable): Page<User> {
        val specification: Specification<User> = Specification.where(null)
        return repository.findAll(specification, pageable)
    }

    fun findById(id: UUID): User? {
       return repository.findById(id).orElse(null)
    }

    @Transactional
    fun createOrder(customerId: UUID, pickupAddress: String, deliveryAddress: String): User? {
        val order = User(
                firstName = pickupAddress,
                lastName = deliveryAddress)
        val entity = repository.save(order)
        return entity
    }

    @Transactional
    fun approvePayment(id: UUID, value: BigDecimal): User? {
        val entity = findById(id)

//        if (!UserValidations.canApprove(value, entity)) {
//            return null
//        }


        //val updatedEntity = repository.save(entity)
        return entity
    }

    @Transactional
    fun refusePayment(id: UUID, reason: String): User? {
        val entity = findById(id)

//        if (!UserValidations.canRefuse(reason, entity)) {
//            return null
//        }
//
//        entity!!.status = UserStatus.REFUSED
//        entity.comment = reason
//        val updatedEntity = repository.save(entity)
        return entity
    }

}