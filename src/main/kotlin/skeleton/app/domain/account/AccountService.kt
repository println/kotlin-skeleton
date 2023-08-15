package skeleton.app.domain.account

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserFilter
import skeleton.app.support.jwt.JwtService

@Service
class AccountService(
        private val repository: AccountRepository,
        private val passwordEncoder: PasswordEncoder,
        private val authenticationManager: AuthenticationManager
) {

    fun findAll(userFilter: UserFilter, pageable: Pageable): Page<Account> {
        val specification: Specification<Account> = Specification.where(null)
        return repository.findAll(specification, pageable)
    }

    fun findById(id: String): Account? {
        return repository.findById(id).orElse(null)
    }

    fun register(email: String, password: String, user: User): Account? {
//        if (!RegisterRequestValidations.canRegister(registerRequest)) {
//            return null
//        }

//        if (findById(registerRequest.email) == null) {
//            return null
//        }

        val entity = Account(
                email = email,
                pass = passwordEncoder.encode(password),
                user = user
        )
        return repository.save(entity)
    }

    fun authenticate(email: String, password: String): Account? {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, password)
        )
        return repository.getReferenceById(email)
    }
}