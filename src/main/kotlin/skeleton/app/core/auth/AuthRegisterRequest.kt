package skeleton.app.core.auth

import skeleton.app.core.user.User


class AuthRegisterRequest (
    val email: String,
    val password: String,
    var user: User
)