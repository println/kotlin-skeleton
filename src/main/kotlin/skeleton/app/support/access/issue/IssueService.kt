package skeleton.app.support.access.issue

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import skeleton.app.domain.user.UserFilter
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.issue.IssuePolicies.assertValidAccount
import skeleton.app.support.access.issue.IssueStatus.*
import skeleton.app.support.access.issue.IssueType.*
import skeleton.app.support.access.issue.email.IssueNotifier
import skeleton.app.support.functions.Generators
import java.time.LocalDateTime
import java.util.*

@Service
class IssueService(
        private val repository: IssueRepository,
        private val accountService: AccountService,
        private val notifier: IssueNotifier
) {
    fun findAll(userFilter: UserFilter, pageable: Pageable): Page<IssueToken> {
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

    @Transactional
    fun createPendencyOfForgotPassword(email: String): IssueToken? {
        val entityAccountOptional = accountService.findByEmail(email)

        if (entityAccountOptional.isEmpty) {
            return null
        }

        val entityAccount = entityAccountOptional.get()
        assertValidAccount(entityAccount)

        accountService.incrementIssues(entityAccount.id!!)

        val entityToken = openPending(entityAccount, FORGOT_PASSWORD)
        notifier.notifyPasswordRecoveryRequested(entityToken, entityAccount)

        return entityToken
    }


    @Transactional
    fun resolvePasswordChange(tokenId: UUID, securityCode: String, password: String): Account? {
        val entityTokenOptional = repository.findByIdAndSecurityCodeAndStatus(tokenId, securityCode)
        IssuePolicies.assertValidRecoveryPassword(entityTokenOptional)

        val entityToken = entityTokenOptional.get()
        val updatedEntityAccount = accountService.changePassword(entityToken.accountId, password)
        accountService.decrementIssues(updatedEntityAccount.id!!)

        closePending(entityToken)
        notifier.notifyPasswordChanged(updatedEntityAccount)

        return updatedEntityAccount
    }

    @Transactional
    fun createPendencyOfAccountActivation(accountId: UUID): IssueToken? {
        val entityAccount = accountService.findById(accountId)
        assertValidAccount(entityAccount)

        accountService.incrementIssues(accountId)

        val entityToken = openPending(entityAccount, ACCOUNT_ACTIVATION)
        notifier.notifyPasswordRecoveryRequested(entityToken, entityAccount)

        return entityToken
    }

    @Transactional
    fun createPendencyOfTemporaryPassword(accountId: UUID): String {
        val entityAccount = accountService.findById(accountId)
        val tempPassword = accountService.assignNewTemporaryPassword(accountId)
        accountService.incrementIssues(entityAccount.id!!)

        val entityToken = openPending(entityAccount, TEMPORARY_PASSWORD)
        notifier.notifyYourAccountHasBeenPasswordResetToATemporaryPassword(entityToken, entityAccount)

        return tempPassword
    }

    @Transactional
    fun resolveTemporaryPassword(tokenId: UUID, password: String): Account {
        val entityTokenOptional = repository.findById(tokenId)
        IssuePolicies.assertValidTempPassword(entityTokenOptional)

        val entityToken = entityTokenOptional.get()
        val updatedEntityAccount = accountService.changePassword(entityToken.accountId, password)

        accountService.decrementIssues(updatedEntityAccount.id!!)

        closePending(entityToken)
        notifier.notifyPasswordChanged(updatedEntityAccount)

        return updatedEntityAccount
    }

    @Transactional
    fun resolveAccountActivation(tokenId: UUID): Account {
        val entityTokenOptional = repository.findById(tokenId)

        IssuePolicies.assertValidAccountActivation(entityTokenOptional)

        val entityToken = entityTokenOptional.get()
        return resolveAccountActivation(entityToken)
    }

    @Transactional
    fun forceResolveAccountActivation(accountId: UUID): Account? {
        val entityTokenOptional = repository.findFirstByAccountIdAndStatus(accountId)

        if (entityTokenOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found")
        }

        val entityToken = entityTokenOptional.get()
        return resolveAccountActivation(entityToken)
    }

    private fun resolveAccountActivation(entityToken: IssueToken): Account {
        val entityAccount = accountService.findById(entityToken.accountId)
        accountService.decrementIssues(entityAccount.id!!)

        closePending(entityToken)
        notifier.notifyAccountHasBeenActivated(entityAccount)

        return entityAccount
    }

    private fun closePending(entityToken: IssueToken) {
        entityToken.status = CLOSED
        repository.save(entityToken)
    }

    private fun openPending(entityAccount: Account, type: IssueType): IssueToken {
        val securityCode = Generators.generateSecurityCode()
        val token = IssueToken(
                accountId = entityAccount.id!!,
                securityCode = securityCode,
                recoveryExpiration = LocalDateTime.now().plus(type.expirationTime),
                type = type)

        return repository.save(token)
    }
}