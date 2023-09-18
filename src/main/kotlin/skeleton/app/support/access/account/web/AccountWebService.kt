package skeleton.app.support.access.account.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.account.AccountFilter
import skeleton.app.support.access.account.AccountRole
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.web.AbstractWebService
import java.util.*


@Service
class AccountWebService(
        private val service: AccountService) : AbstractWebService() {

    fun findAll(filter: AccountFilter, pageable: Pageable): Page<AccountDto> {
        return service.findAll(filter, pageable)
    }

    fun register(register: AccountRegisterDto): AccountDto {
        val nullableEntity = service.register(register)
        return assertBadRequest(nullableEntity)
    }

    fun findById(id: UUID): AccountDto {
        val nullableEntity = service.findById(id)
        return assertNotFound(nullableEntity)
    }

    fun updateCredentials(id: UUID, updateLogin: UpdateLoginDto): AccountDto {
        val nullableEntity = service.updateCredentials(id, updateLogin)
        return assertNotFound(nullableEntity)
    }

    fun changeRole(id: UUID, role: AccountRole): AccountDto {
        val nullableEntity = service.updateRole(id, role)
        val entity = assertNotFound(nullableEntity)
        return AccountDto(entity)
    }

    fun block(id: UUID): AccountDto {
        val nullableEntity = service.block(id)
        val entity = assertNotFound(nullableEntity)
        return AccountDto(entity)
    }

    fun unblock(id: UUID): AccountDto {
        val nullableEntity = service.unblock(id)
        val entity = assertNotFound(nullableEntity)
        return AccountDto(entity)
    }
}