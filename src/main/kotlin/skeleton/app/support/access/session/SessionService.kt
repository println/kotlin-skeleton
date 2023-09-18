package skeleton.app.support.access.session

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import skeleton.app.support.access.account.Account
import java.util.*
import java.util.function.Function

@Service
class SessionService(
        private val repository: SessionRepository) {

    fun findAll(filter: SessionFilter, pageable: Pageable): Page<SessionDto> {
        var pageParam = pageable
        if (!pageable.sort.isSorted) {
            val sort = Sort.by(
                    Sort.Order.asc(Session::revoked.name),
                    Sort.Order.asc(Session::expired.name),
                    Sort.Order.desc(Session::createdAt.name))
            pageParam = PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)
        }

        val specification: Specification<Session> = Specification.where(null)
        val page = repository.findAll(specification, pageParam)
        val converter = Function<Session?, SessionDto> {
            SessionDto(it)
        }
        return page.map(converter);
    }

    fun findById(id: UUID): Optional<SessionDto> {
        return repository.findById(id).map { SessionDto(it) }
    }

    fun start(account: Account, jwtToken: String): SessionDto {
        val session = Session(
                account = account,
                token = jwtToken,
                expired = false,
                revoked = false
        )
        return SessionDto(repository.save(session))
    }

    fun revokeAllSessionsByAccount(accountId: UUID) {
        val entities = repository.findAllByAccountIdAndExpiredIsFalseAndRevokedIsFalse(accountId)

        if (entities.isNotEmpty()) {
            entities.forEach {
                it.expired = true
                it.revoked = true
            }
            repository.saveAll(entities)
        }
    }

    fun save(session: Session): SessionDto {
        return SessionDto(repository.save(session))
    }

    fun findByToken(token: String): Optional<Session> {
        return repository.findByToken(token)
    }
}