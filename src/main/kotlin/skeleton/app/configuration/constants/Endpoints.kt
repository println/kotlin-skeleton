package skeleton.app.configuration.constants

object Endpoints {
    private const val V1 = "/api/v1"
    const val USER = "${V1}/user"
    const val USER_ = "${USER}/"
    const val AUTH = "${V1}/auth"
    const val AUTH_ = "${AUTH}/"
    const val ACCOUNT = "${V1}/account"
    const val ACCOUNT_ = "${ACCOUNT}/"
    const val SESSION = "${V1}/session/"
    const val SESSION_ = "${SESSION}/"
    const val RECOVERY = "${V1}/recovery"
    const val RECOVERY_ = "${RECOVERY}/"
    const val MANAGEMENT = "${V1}/management"
    const val MANAGEMENT_ = "${MANAGEMENT}/"
    const val ADMIN = "${V1}/management"
    const val ADMIN_ = "${ADMIN}/"
}