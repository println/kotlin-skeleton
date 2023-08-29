package skeleton.app.support.access.issue.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints
import java.util.*

@RestController
@RequestMapping(Endpoints.ACCOUNT_ACTIVATION)
class AccountActivationController(
        private val webService: IssueWebService
) {
    @PostMapping("/{token}")
    fun activateAccount(
            @PathVariable("token") token: UUID
    ): ResponseEntity<Unit> {
        webService.resolveAccountActivation(token)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/force/{accountId}")
    @PreAuthorize("hasAuthority('admin:create')")
    fun forceActivateAccount(
            @PathVariable("accountId") accountId: UUID
    ): ResponseEntity<Unit> {
        webService.forceResolveAccountActivation(accountId)
        return ResponseEntity(HttpStatus.OK)
    }
}