package skeleton.app.support.access.session.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import skeleton.app.support.access.session.Session
import skeleton.app.support.access.session.SessionFilter
import skeleton.app.support.access.session.SessionService
import skeleton.app.support.web.AbstractWebService
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