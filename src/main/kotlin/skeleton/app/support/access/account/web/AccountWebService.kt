package skeleton.app.support.access.account.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import skeleton.app.support.access.AccountUser
import skeleton.app.support.access.AccountUserService
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.account.AccountFilter
import skeleton.app.support.access.account.AccountRole
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.issue.IssueService
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.session.SessionDto
import skeleton.app.support.access.session.SessionService
import skeleton.app.support.web.AbstractWebService
import java.util.*


@Service
class AccountWebService(
        private val service: AccountService,
        private val issueService: IssueService,
        private val sessionService: SessionService,
        private val userService: AccountUserService
) : AbstractWebService() {

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

    fun getActiveSessions(id: UUID): Collection<SessionDto> {
        return sessionService.getAllActiveByAccountId(id)
    }

    fun getOpenIssues(id: UUID): Collection<IssueToken> {
        return issueService.getAllOpenByAccountId(id)
    }

    fun getUser(id: UUID): AccountUser {
        return userService.findByAccountId(id)
    }
}