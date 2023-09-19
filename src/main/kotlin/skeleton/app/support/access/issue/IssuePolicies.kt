package skeleton.app.support.access.issue

import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.account.AccountStatus
import java.time.LocalDateTime
import java.util.*

object IssuePolicies {
    const val EXPIRATION_FORGOT_PASSWORD_HOURS = 2L
    const val EXPIRATION_TEMPORARY_PASSWORD_DAYS = 30L
    const val EXPIRATION_ACCOUNT_ACTIVATION_DAYS = 30L

    fun assertValidAccount(account: AccountDto){
        if(account.status ==  AccountStatus.BLOCKED){
            throw ResponseStatusException(FORBIDDEN, "You cannot proceed with your request")
        }
    }
    fun assertValidRecoveryPassword(issueTokenOptional: Optional<IssueToken>) {
        if (issueTokenOptional.isEmpty) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session not found")
        }

        val issueToken = issueTokenOptional.get()

        if (issueToken.type != IssueType.FORGOT_PASSWORD) {
            return
        }

        if (LocalDateTime.now().isAfter(issueToken.recoveryExpiration)) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session has been expired!")
        }
    }

    fun assertValidTempPassword(issueTokenOptional: Optional<IssueToken>) {
        if (issueTokenOptional.isEmpty) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session not found")
        }

        val issueToken = issueTokenOptional.get()

        if (issueToken.type != IssueType.TEMPORARY_PASSWORD) {
            return
        }

        if (LocalDateTime.now().isAfter(issueToken.recoveryExpiration)) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session has been expired!")
        }
    }

    fun assertValidAccountActivation(issueTokenOptional: Optional<IssueToken>) {
        if (issueTokenOptional.isEmpty) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session not found")
        }

        val issueToken = issueTokenOptional.get()

        if (issueToken.type != IssueType.ACCOUNT_ACTIVATION) {
            return
        }

        if (LocalDateTime.now().isAfter(issueToken.recoveryExpiration)) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session has been expired!")
        }
    }
}