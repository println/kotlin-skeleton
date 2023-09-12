package skeleton.app.support.access.issue.web

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints.RESET_PASSWORD


@RestController
@RequestMapping(RESET_PASSWORD)
class ResetPasswordController(
        private val webService: IssueWebService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    fun assignTempPassword(
            @Valid @RequestBody data: ResetPasswordAccountDto): ResponseEntity<ResetPasswordTemporaryDto> {
        val entity = webService.resetPassword(data.accountId)
        return ResponseEntity(entity, CREATED)
    }

    @PostMapping("/renew")
    fun resolveTempPassword(
            @Valid @RequestBody data: ResetPasswordDto
    ): ResponseEntity<Unit> {
        webService.resolveResetPassword(data)
        return ResponseEntity(OK)
    }
}