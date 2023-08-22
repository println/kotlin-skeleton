package skeleton.app.support.access.session.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserFilter
import skeleton.app.domain.user.UserService
import skeleton.app.support.access.session.Session
import skeleton.app.support.access.session.SessionFilter
import skeleton.app.support.access.session.SessionService
import skeleton.app.support.web.AbstractWebService
import java.math.BigDecimal
import java.util.*

@Service
class SessionWebService(private val service: SessionService): AbstractWebService<Session>() {
    fun findAll(filter: SessionFilter, pageable: Pageable): Page<Session> {
        return service.findAll(filter, pageable)
    }

    fun findById(id: UUID): Session {
        val nullableEntity = service.findById(id)
        return assertNotFound(nullableEntity.orElse(null))
    }
}