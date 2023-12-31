package skeleton.app.support.access.issue.web

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import skeleton.app.configuration.constants.Endpoints.FORGOT_PASSWORD


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

    @PostMapping("/renew")
    fun changePassword(
            @Valid @RequestBody data: ForgotPasswordDto
    ): ResponseEntity<Unit> {
        webService.resolveForgotPassword(data)
        return ResponseEntity(OK)
    }

    @GetMapping("/code/{code}")
    fun findTokenBySecurityCode(
            @PathVariable("code") code: String
    ): ForgotPasswordTokenDto {
        val data = ForgotPasswordSecurityCodeDto(code)
        return webService.getResetPasswordTokenBySecurityCode(data)
    }
}