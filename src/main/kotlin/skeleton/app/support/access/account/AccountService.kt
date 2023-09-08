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
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import skeleton.app.domain.user.UserFilter
import skeleton.app.support.access.AccountUserService
import skeleton.app.support.access.account.web.AccountRegisterDto
import skeleton.app.support.access.account.web.UpdateLoginDto
import skeleton.app.support.access.login.Login
import skeleton.app.support.functions.Generators
import java.util.*

@Service
class AccountService(
        private val repository: AccountRepository,
        private val passwordEncoder: PasswordEncoder,
        private val authenticationManager: AuthenticationManager,
        private val accountUserService: AccountUserService
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

    fun findByEmail(email: String): Optional<Account?> {
        return repository.findByEmail(email.lowercase())
    }

    @Transactional
    fun register(@Valid register: AccountRegisterDto): Account? {
        AccountPolicies.assertName(register.firstName)
        AccountPolicies.assertName(register.lastName)
        AccountPolicies.assertEmail(register.email.lowercase())
        AccountPolicies.assertPassword(register.password)

        if (repository.existsByEmail(register.email.lowercase())) {
            return null
        }

        val account = Account(
                "${register.firstName} ${register.lastName}",
                register.email.lowercase(),
                Login(register.email.lowercase(), "")
        )
        setPassword(account, register.password)
        val entityAccount = repository.save(account)

        accountUserService.create(entityAccount, register.firstName, register.lastName)

        return entityAccount
    }


    fun updateCredentials(id: UUID, @Valid updateLogin: UpdateLoginDto): Account {
        AccountPolicies.assertEmail(updateLogin.email.lowercase())
        AccountPolicies.assertPassword(updateLogin.password)

        val entity = findById(id)
        entity.email = updateLogin.email.lowercase()
        entity.login.username = updateLogin.email.lowercase()
        setPassword(entity, updateLogin.password)
        return repository.save(entity)
    }

    @Transactional
    fun updateRole(id: UUID, role: AccountRole): Account {
        val entity = findById(id)
        entity.role = role
        return repository.save(entity)
    }

    @Transactional
    fun incrementIssues(id: UUID): Account {
        val entity = findById(id)
        entity.issues += 1
        return repository.save(entity)
    }

    @Transactional
    fun decrementIssues(id: UUID): Account {
        val entity = findById(id)
        entity.issues = if (entity.issues <= 0) 0 else entity.issues - 1
        return repository.save(entity)
    }

    @Transactional
    fun block(id: UUID): Account {
        return updateStatus(id, AccountStatus.BLOCKED)
    }

    @Transactional
    fun unblock(id: UUID): Account {
        return updateStatus(id, AccountStatus.ACTIVE)
    }

    @Transactional
    fun changePassword(id: UUID, password: String): Account {
        val entity = findById(id)
        setPassword(entity, password)
        return repository.save(entity)
    }

    @Transactional
    fun assignNewTemporaryPassword(id: UUID): String {
        val entity = findById(id)
        val tempPassword = Generators.generatePassword()
        setPassword(entity, tempPassword)
        repository.save(entity)
        return tempPassword
    }

    @Transactional
    fun authenticate(email: String, password: String): Account {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email.lowercase(), password)
        )
        return repository.findByEmail(email.lowercase()).get()
    }


    private fun setPassword(account: Account, password: String) {
        AccountPolicies.assertPassword(password)
        account.login.password = passwordEncoder.encode(password)
    }

    private fun updateStatus(id: UUID, status: AccountStatus): Account {
        val entity = findById(id)
        entity.status = status
        return repository.save(entity)
    }
}