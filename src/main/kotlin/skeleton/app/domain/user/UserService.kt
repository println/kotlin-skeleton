package skeleton.app.domain.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import skeleton.app.support.access.AccountUserService
import skeleton.app.support.access.account.Account
import java.math.BigDecimal
import java.util.*

@Service
class UserService(
        private val repository: UserRepository): AccountUserService {

    fun findAll(userFilter: UserFilter, pageable: Pageable): Page<User> {
        val specification: Specification<User> = Specification.where(null)
        return repository.findAll(specification, pageable)
    }

    fun findById(id: UUID): User? {
        val entityOptional = repository.findById(id)
        if (entityOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found")
        }
        return entityOptional.get()
    }

    override fun findByAccount(account: Account): User {
       return findByAccountId(account.id!!)
    }

    override fun findByAccountId(accountId: UUID): User {
        val entityOptional = repository.findFirstByAccountId(accountId)
        if (entityOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found")
        }
        return entityOptional.get()
    }

    @Transactional
    override fun create(account: Account, firstName: String, lastName: String): User {
        val order = User(
                firstName,
                lastName,
                account.id!!)
        return repository.save(order)
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