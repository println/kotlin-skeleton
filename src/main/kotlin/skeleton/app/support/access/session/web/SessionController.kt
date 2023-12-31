package skeleton.app.support.access.session.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints.SESSION
import skeleton.app.configuration.constants.Endpoints.SESSION_
import skeleton.app.support.access.session.SessionDto
import skeleton.app.support.access.session.SessionFilter
import java.util.*

@RequestMapping(SESSION, SESSION_)
@RestController
class SessionController(
        private val service: SessionWebService
) {

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(
            pageable: Pageable): Page<SessionDto> {
        val filter = SessionFilter()
        return service.findAll(filter, pageable)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getById(
            @PathVariable("id") id: UUID): SessionDto {
        return service.findById(id)
    }
}