package skeleton.app.support.access.issue.web

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import skeleton.app.configuration.constants.Endpoints.RESET_PASSWORD
import java.util.*


@RestController
@RequestMapping(RESET_PASSWORD)
class ResetPasswordController(
        private val webService: IssueWebService
) {

    @PostMapping("/{accountId}")
    @PreAuthorize("hasAuthority('admin:create')")
    fun assignTempPassword(
            @PathVariable("accountId") id: UUID): ResetPasswordTemporaryDto {
        return webService.resetPassword(id)
    }

    @PostMapping("/renew/{token}")
    fun resolveTempPassword(
            @PathVariable("token") token: UUID,
            @Valid @RequestBody data: ResetPasswordDto
    ): ResponseEntity<Unit> {
        webService.resolveResetPassword(token, data)
        return ResponseEntity(OK)
    }
}