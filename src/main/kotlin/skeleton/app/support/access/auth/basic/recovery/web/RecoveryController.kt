package skeleton.app.support.access.auth.basic.recovery.web

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import skeleton.app.configuration.constants.Endpoints.RECOVERY
import skeleton.app.configuration.constants.Endpoints.RECOVERY_
import java.util.*


@RestController
@RequestMapping(RECOVERY, RECOVERY_)
class RecoveryController(
        private val webService: RecoveryWebService
) {
    @PostMapping("/forgot")
    fun forgot(
            @Valid @RequestBody data: RecoveryEmailDto
    ): ResponseEntity<Unit> {
        webService.forgot(data)
        return ResponseEntity<Unit>(CREATED)
    }

    @PostMapping("/change-password/{token}")
    fun changePassword(
            @PathVariable("token") token: UUID,
            @Valid @RequestBody data: RecoveryPasswordDto
    ): ResponseEntity<Unit> {
        webService.changePassword(token, data)
        return ResponseEntity<Unit>(OK)
    }
}