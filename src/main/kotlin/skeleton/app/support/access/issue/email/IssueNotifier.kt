package skeleton.app.support.access.issue.email

import org.springframework.stereotype.Component
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.notification.NotificationService

@Component
class IssueNotifier(
        private val notificationService: NotificationService
) {
    fun notifyPasswordRecoveryRequested(token: IssueToken, account: AccountDto) {
        val recipients = listOf(account.email)
        val subject = "Password Recovery"
        val message = """
            Access the link to set a new password:
            
            ${Endpoints.FORGOT_PASSWORD}/change-password/${token.id}
            
            Security code is: ${token.securityCode}
        """.trimIndent()
        notificationService.notifyByEmail(recipients, subject, message)
    }

    fun notifyYourAccountHasBeenPasswordResetToATemporaryPassword(token: IssueToken, account: AccountDto) {
        val recipients = listOf(account.email)
        val subject = "Your account has been password reset to a temporary password"
        val message = """
           You must log in to the system with a temporary password and enter a new password.
        """.trimIndent()
        notificationService.notifyByEmail(recipients, subject, message)
    }

    fun notifyPasswordChanged(account: Account) {
        val recipients = listOf(account.email)
        val subject = "Your password has been changed!"
        val message = """
            Your system password was recently changed.
        """.trimIndent()
        notificationService.notifyByEmail(recipients, subject, message)
    }

    fun notifyAccountHasBeenActivated(account: AccountDto) {
        val recipients = listOf(account.email)
        val subject = "Your Account has been activated successfully!"
        val message = """
            You can explorer all features now!
        """.trimIndent()
        notificationService.notifyByEmail(recipients, subject, message)
    }
}