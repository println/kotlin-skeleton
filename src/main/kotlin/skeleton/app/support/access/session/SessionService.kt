package skeleton.app.support.access.session

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import skeleton.app.support.access.account.Account
import java.util.*

@Service
class SessionService(
        private val repository: SessionRepository) {

    fun findAll(filter: SessionFilter, pageable: Pageable): Page<Session> {
        val specification: Specification<Session> = Specification.where(null)
        return repository.findAll(specification, pageable)
    }

    fun findById(id: UUID): Optional<Session?> {
        return repository.findById(id)
    }

    fun add(account: Account, jwtToken: String): Session {
        val session = Session(
                accountId = account.id!!,
                token = jwtToken,
                expired = false,
                revoked = false
        )
        return repository.save(session);
    }

    fun revokeAllUserTokens(account: Account) {
        val entities = repository.findAllByAccountIdAndExpiredIsFalseAndRevokedIsFalse(account.id)

        if (entities.isNotEmpty()) {
            entities.forEach {
                it.expired = true
                it.revoked = true
            }
            repository.saveAll(entities)
        }
    }

    fun save(session: Session): Session {
        return repository.save(session);
    }

    fun findByToken(token: String): Optional<Session> {
        return repository.findByToken(token)
    }
}