package skeleton.app.support.access.issue.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.support.access.issue.IssueFilter
import skeleton.app.support.access.issue.IssueToken
import java.util.*

@RestController
@RequestMapping(Endpoints.ACCOUNT_ISSUE)
@PreAuthorize("hasRole('ADMIN')")
class IssueController(
        private val webService: IssueWebService
) {
    @GetMapping
    @PreAuthorize("hasAuthority('admin:read')")
    fun getAll(
            pageable: Pageable): Page<IssueToken> {
        val filter = IssueFilter()
        return webService.findAll(filter, pageable)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:read')")
    fun getById(
            @PathVariable("id") id: UUID): IssueToken {
        return webService.findById(id)
    }
}