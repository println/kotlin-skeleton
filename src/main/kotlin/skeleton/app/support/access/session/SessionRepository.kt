package skeleton.app.support.access.session

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface SessionRepository : JpaRepository<Session, UUID>, JpaSpecificationExecutor<Session> {

    fun findAllByAccountIdAndExpiredIsFalseAndRevokedIsFalse(id: UUID?): List<Session>

    fun findByToken(token: String): Optional<Session>

    fun existsByAccountIdAndExpiredIsFalseAndRevokedIsFalse(id: UUID?): Boolean
}
