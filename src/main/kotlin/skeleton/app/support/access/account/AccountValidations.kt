package skeleton.app.support.access.account

import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserValidations
import skeleton.app.support.access.auth.basic.auth.AuthRegisterRequest

object AccountValidations {
    fun canRegister(entity: AuthRegisterRequest?) =
        (entity != null)

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