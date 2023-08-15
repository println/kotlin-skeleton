package skeleton.app.domain.account.web

import skeleton.app.domain.user.User


class RegisterRequestDTO (
    val email: String,
    val password: String,
    var user: User
)