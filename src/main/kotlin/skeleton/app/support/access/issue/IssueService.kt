package skeleton.app.support.access.issue

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.issue.IssuePolicies.assertValidAccount
import skeleton.app.support.access.issue.IssueType.*
import skeleton.app.support.access.issue.email.IssueNotifier
import skeleton.app.support.functions.Generators
import java.time.LocalDateTime
import java.util.*

@Service
class IssueService(private val repository: IssueRepository, private val accountService: AccountService, private val notifier: IssueNotifier) {
    fun findAll(filter: IssueFilter, pageable: Pageable): Page<IssueToken> {
        val specification: Specification<IssueToken> = Specification.where(null)
        return repository.findAll(specification, pageable)
    }

    fun findById(id: UUID): IssueToken {
        val entityOptional = repository.findById(id)
        if (entityOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found")
        }
        return entityOptional.get()
    }

    fun findForgotPasswordTokenBySecurityCode(securityCode: String): UUID? {
        val entityOptional = repository.findFirstBySecurityCodeAndType(securityCode, FORGOT_PASSWORD)
        if (entityOptional.isEmpty) {
            return null
        }
        IssuePolicies.assertValidRecoveryPassword(entityOptional)
        return entityOptional.get().id!!
    }

    fun getAllOpenByAccountId(accountId: UUID): Collection<IssueToken> {
        return repository.findAllByAccountIdAndStatus(accountId)
    }

    @Transactional
    fun createPendencyOfForgotPassword(email: String): IssueToken? {
        val entityAccountOptional = accountService.findByEmail(email)

        if (entityAccountOptional.isEmpty) {
            return null
        }

        val entityAccount = entityAccountOptional.get()
        assertValidAccount(entityAccount)

        val entityToken = openPending(entityAccount.id!!, FORGOT_PASSWORD)
        notifier.notifyPasswordRecoveryRequested(entityToken, entityAccount)

        return entityToken
    }

    @Transactional
    fun resolvePasswordChange(tokenId: UUID, password: String): Account? {
        val entityTokenOptional = repository.findByIdAndStatus(tokenId)
        IssuePolicies.assertValidRecoveryPassword(entityTokenOptional)

        val entityToken = entityTokenOptional.get()
        val updatedEntityAccount = accountService.changePassword(entityToken.accountId, password)

        closePending(entityToken)
        notifier.notifyPasswordChanged(updatedEntityAccount)

        return updatedEntityAccount
    }

    @Transactional
    fun createPendencyOfAccountActivation(accountId: UUID): IssueToken? {
        val entityAccount = accountService.findById(accountId)
        assertValidAccount(entityAccount)

        val entityToken = openPending(entityAccount.id!!, ACCOUNT_ACTIVATION)
        notifier.notifyPasswordRecoveryRequested(entityToken, entityAccount)

        return entityToken
    }

    @Transactional
    fun createPendencyOfTemporaryPassword(accountId: UUID): String {
        lateinit var entityAccount: AccountDto

        try {
            entityAccount = accountService.findById(accountId)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Id not found")
        }

        val tempPassword = accountService.assignNewTemporaryPassword(accountId)

        val entityToken = openPending(entityAccount.id!!, TEMPORARY_PASSWORD)
        notifier.notifyYourAccountHasBeenPasswordResetToATemporaryPassword(entityToken, entityAccount)

        return tempPassword
    }

    @Transactional
    fun resolveTemporaryPassword(tokenId: UUID, password: String): Account {
        val entityTokenOptional = repository.findById(tokenId)
        IssuePolicies.assertValidTempPassword(entityTokenOptional)

        val entityToken = entityTokenOptional.get()
        val updatedEntityAccount = accountService.changePassword(entityToken.accountId, password)

        closePending(entityToken)
        notifier.notifyPasswordChanged(updatedEntityAccount)

        return updatedEntityAccount
    }

    @Transactional
    fun resolveAccountActivation(tokenId: UUID): AccountDto {
        val entityTokenOptional = repository.findById(tokenId)

        IssuePolicies.assertValidAccountActivation(entityTokenOptional)

        val entityToken = entityTokenOptional.get()
        return resolveAccountActivation(entityToken)
    }

    @Transactional
    fun forceResolveAccountActivation(accountId: UUID): AccountDto? {
        val entityTokenOptional = repository.findFirstByAccountIdAndStatus(accountId)

        if (entityTokenOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found")
        }

        val entityToken = entityTokenOptional.get()
        return resolveAccountActivation(entityToken)
    }

    private fun resolveAccountActivation(entityToken: IssueToken): AccountDto {
        val entityAccount = accountService.findById(entityToken.accountId)

        closePending(entityToken)
        notifier.notifyAccountHasBeenActivated(entityAccount)

        return entityAccount
    }

    private fun closePending(entityToken: IssueToken) {
        if(entityToken.type == FORGOT_PASSWORD){
            closeOlder(entityToken.accountId, TEMPORARY_PASSWORD)
        }
        closeOlder(entityToken.accountId, entityToken.type)
    }

    private fun openPending(accountId: UUID, type: IssueType): IssueToken {
        val securityCode = Generators.generateSecurityCode()
        val token = IssueToken(accountId = accountId, securityCode = securityCode, recoveryExpiration = LocalDateTime.now().plus(type.expirationTime), type = type)

        closeOlder(accountId, type)

        accountService.incrementIssues(accountId)
        return repository.save(token)
    }

    private fun closeOlder(accountId: UUID, type: IssueType){
        val rowsAffected = repository.closeOlderIssues(accountId, type)
        repeat(rowsAffected) { accountService.decrementIssues(accountId) }
    }


}