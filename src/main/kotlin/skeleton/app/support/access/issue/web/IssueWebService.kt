package skeleton.app.support.access.issue.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.issue.IssueFilter
import skeleton.app.support.access.issue.IssueService
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.web.AbstractWebService
import java.util.*

@Service
class IssueWebService(
        private val service: IssueService
) : AbstractWebService() {

    fun findAll(filter: IssueFilter, pageable: Pageable): Page<IssueToken> {
        return service.findAll(filter, pageable)
    }

    fun findById(id: UUID): IssueToken {
        val nullableEntity = service.findById(id)
        return assertNotFound(nullableEntity)
    }

    fun forgotPassword(data: ForgotPasswordEmailDto) {
        service.createPendencyOfForgotPassword(data.email)
    }

    fun resolveForgotPassword(data: ForgotPasswordDto): AccountDto {
        val nullableEntity = service.resolvePasswordChange(data.token, data.password)
        val entity: Account = assertNotFound(nullableEntity)
        return AccountDto(entity)
    }

    fun resetPassword(id: UUID): ResetPasswordTemporaryDto {
        return ResetPasswordTemporaryDto(service.createPendencyOfTemporaryPassword(id))
    }

    fun resolveResetPassword(data: ResetPasswordDto): AccountDto {
        val nullableEntity = service.resolveTemporaryPassword(data.tokenId, data.password)
        val entity: Account = assertNotFound(nullableEntity)
        return AccountDto(entity)
    }

    fun resolveAccountActivation(token: UUID): AccountDto {
        val nullableEntity = service.resolveAccountActivation(token)
        val entity: AccountDto = assertNotFound(nullableEntity)
        return entity
    }

    fun forceResolveAccountActivation(accountId: UUID): AccountDto {
        val nullableEntity = service.forceResolveAccountActivation(accountId)
        val entity: AccountDto = assertNotFound(nullableEntity)
        return entity
    }

    fun getResetPasswordTokenBySecurityCode(data: ForgotPasswordSecurityCodeDto): ForgotPasswordTokenDto {
        val nullableEntity = service.findForgotPasswordTokenBySecurityCode(data.securityCode)
        val entity: UUID = assertNotFound(nullableEntity)
        return ForgotPasswordTokenDto(entity)
    }
}