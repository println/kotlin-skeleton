package skeleton.app.core.account

import skeleton.app.core.auth.AuthRegisterRequest
import skeleton.app.core.user.User
import skeleton.app.core.user.UserValidations

object AccountValidations {
    fun canRegister(entity: AuthRegisterRequest?) =
        (entity != null)
            .and(isValidEmail(entity!!.email))
            .and(isValidPassword(entity.password))
            .and(isValidUser(entity.user))

    private fun isValidEmail(email: String): Boolean {
        return true
    }

    private fun isValidPassword(password: String): Boolean {
        return true
    }

    private fun isValidUser(user: User?): Boolean {
        return UserValidations.canUpdate(user)
    }
}