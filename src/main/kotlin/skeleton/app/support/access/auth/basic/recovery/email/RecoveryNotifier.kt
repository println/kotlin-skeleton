package skeleton.app.support.access.auth.basic.recovery.email

import org.springframework.stereotype.Component
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.auth.basic.recovery.RecoveryToken
import skeleton.app.support.notification.NotificationService

@Component
class RecoveryNotifier(
        private val notificationService: NotificationService
) {
    fun notifyPasswordRecoveryRequested(token: RecoveryToken, account: Account) {
        val recipients = listOf(account.email)
        val subject = "Password Recovery"
        val message = """
            Access the link to set a new password:
            
            ${Endpoints.RECOVERY}/change-password/${token.id}
            
            Security code is: ${token.securityCode}
        """.trimIndent()
        notificationService.notifyByEmail(recipients, subject, message)
    }

    fun notifyPasswordChanged(account: Account) {
        val recipients = listOf(account.email)
        val subject = "Your password has been changed!"
        val message = """
            Your DocuSign password was recently changed.
        """.trimIndent()
        notificationService.notifyByEmail(recipients, subject, message)
    }
}