package skeleton.app.domain.auth

enum class AuthStatus {
    WAITING_PAYMENT, REFUSED, ACCEPTED;

    fun canChangeTo(status: AuthStatus) = when (this) {
        WAITING_PAYMENT -> arrayOf(
                REFUSED,
                ACCEPTED
        )
        else -> arrayOf()
    }.contains(status)
}