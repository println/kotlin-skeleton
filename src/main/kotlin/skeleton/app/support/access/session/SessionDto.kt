package skeleton.app.support.access.session

import java.time.LocalDateTime
import java.util.*

class SessionDto(
        session: Session,
        val id: UUID = session.id!!,
        val revoked: Boolean = session.revoked,
        val expired: Boolean = session.expired,
        val accountId: UUID = session.account.id!!,
        val accountUsername: String = session.account.username,
        val createdAt: LocalDateTime = session.createdAt!!
)
