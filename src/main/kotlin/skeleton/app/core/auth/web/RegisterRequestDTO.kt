package skeleton.app.core.auth.web

import skeleton.app.core.user.User


class RegisterRequestDTO (
    val email: String,
    val password: String,
    var user: User
)