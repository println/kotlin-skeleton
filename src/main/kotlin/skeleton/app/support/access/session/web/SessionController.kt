package skeleton.app.support.access.session.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.domain.user.User
import skeleton.app.support.access.session.Session
import skeleton.app.support.access.session.SessionFilter
import skeleton.app.support.access.session.SessionService
import java.util.*

@RequestMapping(Endpoints.SESSION, Endpoints.SESSION_)
@RestController
class SessionController(
        private val service: SessionWebService
) {

    @GetMapping
    fun getAll(
            pageable: Pageable): Page<Session> {
        val filter = SessionFilter()
        return service.findAll(filter, pageable)
    }

    @GetMapping("/{id}")
    fun getById(
            @PathVariable("id") id: UUID): Session {
        return service.findById(id)
    }
}