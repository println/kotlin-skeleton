package skeleton.app.domain.account

import skeleton.app.domain.account.web.RegisterRequestDTO
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserValidations

object RegisterRequestValidations {
    fun canRegister(entity: RegisterRequestDTO?) =
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