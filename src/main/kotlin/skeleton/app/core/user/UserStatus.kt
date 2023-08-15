package skeleton.app.core.user

enum class UserStatus {
    WAITING_PAYMENT, REFUSED, ACCEPTED;

    fun canChangeTo(status: UserStatus) = when (this) {
        WAITING_PAYMENT -> arrayOf(
                REFUSED,
                ACCEPTED
        )
        else -> arrayOf()
    }.contains(status)
}