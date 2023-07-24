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

    fun findAll(userFilter: UserFilter, pageable: Pageable): Page<Userr> {
        val specification: Specification<Userr> = Specification.where(null)
        return repository.findAll(specification, pageable)
    }

    fun findAllByIdAndCustomerId(customerId: UUID, userFilter: UserFilter, pageable: Pageable): Page<Userr> {
        val specification: Specification<Userr> = Specification.where(null)
        return repository.findAllByCustomerId(customerId, specification, pageable)
    }

    fun findById(id: UUID): Userr? {
       return repository.findById(id).orElse(null)
    }

    @Transactional
    fun createOrder(customerId: UUID, pickupAddress: String, deliveryAddress: String): Userr? {
        val order = Userr(
                customerId = customerId,
                pickupAddress = pickupAddress,
                deliveryAddress = deliveryAddress)
        val entity = repository.save(order)
        return entity
    }

    @Transactional
    fun approvePayment(id: UUID, value: BigDecimal): Userr? {
        val entity = findById(id)

        if (!UserValidations.canApprove(value, entity)) {
            return null
        }

        entity!!.status = UserStatus.ACCEPTED
        entity.value = value
        val updatedEntity = repository.save(entity)
        return updatedEntity
    }

    @Transactional
    fun refusePayment(id: UUID, reason: String): Userr? {
        val entity = findById(id)

        if (!UserValidations.canRefuse(reason, entity)) {
            return null
        }

        entity!!.status = UserStatus.REFUSED
        entity.comment = reason
        val updatedEntity = repository.save(entity)
        return updatedEntity
    }

}