package skeleton.app.support.access.auth.basic.recovery

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountService
import skeleton.app.support.access.auth.basic.recovery.email.RecoveryNotifier
import java.util.*

@Service
class RecoveryService(
        private val repository: RecoveryRepository,
        private val accountService: AccountService,
        private val notifier: RecoveryNotifier
) {

    @Transactional
    fun forgot(email: String): RecoveryToken? {
        val entityAccountOptional = accountService.findByEmail(email)

        if (entityAccountOptional.isEmpty) {
            return null
        }

        val entityAccount = entityAccountOptional.get()
        val securityCode = generateSecurityCode()
        val token = RecoveryToken(entityAccount.id!!, securityCode)

        val entityToken = repository.save(token)

        notifier.notifyPasswordRecoveryRequested(entityToken, entityAccount)

        return entityToken
    }

    @Transactional
    fun changePassword(tokenId: UUID, securityCode: String, password: String): Account? {
        val entityTokenOptional = repository.findByIdAndSecurityCodeAndStatus(tokenId, securityCode)
        RecoveryPolicies.assertValidToken(entityTokenOptional)

        val entityToken = entityTokenOptional.get()
        val updatedEntityAccount = accountService.changePassword(entityToken.accountId, password)

        entityToken.status = RecoveryTokenStatus.CLOSED
        repository.save(entityToken)

        notifier.notifyPasswordChanged(updatedEntityAccount)

        return updatedEntityAccount
    }

    private fun generateSecurityCode(length: Int = 4) = (0..9).shuffled().take(length).joinToString()

}