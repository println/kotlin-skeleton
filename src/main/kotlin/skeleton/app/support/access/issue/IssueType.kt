package skeleton.app.support.access.issue

import java.time.Duration

enum class IssueType(
        val expirationTime: Duration
) {
    ACCOUNT_ACTIVATION(Duration.ofDays(IssuePolicies.EXPIRATION_ACCOUNT_ACTIVATION_DAYS)),
    FORGOT_PASSWORD(Duration.ofDays(IssuePolicies.EXPIRATION_FORGOT_PASSWORD_HOURS)),
    TEMPORARY_PASSWORD(Duration.ofDays(IssuePolicies.EXPIRATION_TEMPORARY_PASSWORD_DAYS))
}