package skeleton.app.support.access.account.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import skeleton.app.domain.user.UserFilter
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountRole
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.web.AbstractWebService
import java.util.*
import java.util.function.Function


@Service
class AccountWebService(
        private val service: AccountService) : AbstractWebService() {

    fun findAll(filter: UserFilter, pageable: Pageable): Page<AccountDto> {
        val page = service.findAll(filter, pageable)
        val converter = Function<Account?, AccountDto> {
            AccountDto(it)
        }
        return page.map(converter);
    }

    fun register(register: AccountRegisterDto): AccountDto {
        val nullableEntity = service.register(register)
        val entity = assertBadRequest(nullableEntity)
        return AccountDto(entity)
    }

    fun findById(id: UUID): AccountDto {
        val nullableEntity = service.findById(id)
        val entity = assertNotFound(nullableEntity)
        return AccountDto(entity)
    }

    fun updateCredentials(id: UUID, updateLogin: UpdateLoginDto): AccountDto {
        val nullableEntity = service.updateCredentials(id, updateLogin)
        val entity = assertNotFound(nullableEntity)
        return AccountDto(entity)
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