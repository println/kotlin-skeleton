package skeleton.app.support.access.issue.web

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import skeleton.app.configuration.constants.Endpoints.FORGOT_PASSWORD
import java.util.*


@RestController
@RequestMapping(FORGOT_PASSWORD)
class ForgotPasswordController(
        private val webService: IssueWebService
) {
    @PostMapping
    fun forgot(
            @Valid @RequestBody data: ForgotPasswordEmailDto
    ): ResponseEntity<Unit> {
        webService.forgotPassword(data)
        return ResponseEntity(CREATED)
    }

    @PostMapping("/renew/{token}")
    fun changePassword(
            @PathVariable("token") token: UUID,
            @Valid @RequestBody data: ForgotPasswordDto
    ): ResponseEntity<Unit> {
        webService.resolveForgotPassword(token, data)
        return ResponseEntity(OK)
    }
}