package skeleton.app.support.access.issue.web

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints
import java.util.*

@RestController
@RequestMapping(Endpoints.ACCOUNT_ACTIVATION)
class AccountActivationController(
        private val webService: IssueWebService
) {
    @GetMapping("/{tokenId}")
    fun activateAccount(
            @PathVariable("tokenId") token: UUID
    ): ResponseEntity<Unit> {
        try {
            webService.resolveAccountActivation(token)
        } catch (_: Exception) {
        }
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/force/{accountId}")
    @PreAuthorize("hasAuthority('admin:create')")
    fun forceActivateAccount(
            @PathVariable("accountId") accountId: UUID
    ): ResponseEntity<Unit> {
        webService.forceResolveAccountActivation(accountId)
        return ResponseEntity(HttpStatus.OK)
    }
}