package skeleton.app.support.access.account

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import skeleton.app.domain.user.UserFilter
import skeleton.app.support.access.account.web.AccountRegisterDto
import skeleton.app.support.access.account.web.UpdateInfoDto
import skeleton.app.support.access.account.web.UpdateLoginDto
import skeleton.app.support.access.auth.basic.login.Login
import java.util.*

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

    fun findById(id: UUID): Account {
        val entityOptional = repository.findById(id)
        if (entityOptional.isEmpty) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found")
        }
        return entityOptional.get()
    }

    fun register(@Valid register: AccountRegisterDto): Account? {
        AccountPolicies.assertName(register.firstName)
        AccountPolicies.assertName(register.lastName)
        AccountPolicies.assertEmail(register.email)
        AccountPolicies.assertPassword(register.password)

        if (repository.existsByEmail(register.email)) {
            return null
        }

        val account = Account(
                register.firstName,
                register.lastName,
                register.email,
                Login(
                        register.email,
                        "")
        )
        setPassword(account, register.password)
        return repository.save(account)
    }

    fun update(id: UUID, @Valid updateInfo: UpdateInfoDto): Account {
        AccountPolicies.assertName(updateInfo.firstName)
        AccountPolicies.assertName(updateInfo.lastName)

        val entity = findById(id)
        entity.firstName = updateInfo.firstName
        entity.lastName = updateInfo.lastName
        return repository.save(entity)
    }

    fun updateCredentials(id: UUID, @Valid updateLogin: UpdateLoginDto): Account {
        AccountPolicies.assertEmail(updateLogin.email)
        AccountPolicies.assertPassword(updateLogin.password)

        val entity = findById(id)
        entity.email = updateLogin.email
        entity.login.username = updateLogin.email
        setPassword(entity, updateLogin.password)
        return repository.save(entity)
    }

    fun authenticate(email: String, password: String): Account {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, password)
        )
        return repository.findByEmail(email).get()
    }

    fun findByEmail(email: String): Optional<Account?> {
        return repository.findByEmail(email)
    }

    fun changePassword(id: UUID, password: String): Account {
        val entity = findById(id)
        setPassword(entity, password)
        return repository.save(entity)
    }

    private fun setPassword(account: Account, password: String) {
        AccountPolicies.assertPassword(password)
        account.login.password = passwordEncoder.encode(password)
    }
}