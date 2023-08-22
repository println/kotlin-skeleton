package skeleton.app.support.access.auth.basic.recovery.web

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import skeleton.app.configuration.constants.Endpoints
import java.util.*


@RestController
@RequestMapping(Endpoints.RECOVERY, Endpoints.RECOVERY_)
class RecoveryController(
        private val webService: RecoveryWebService
) {
    @PostMapping("/forgot")
    fun forgot(
            @Valid @RequestBody data: RecoveryEmailDto
    ): ResponseEntity<Unit> {
        webService.forgot(data)
        return ResponseEntity<Unit>(HttpStatus.CREATED)
    }

    @PostMapping("/change-password/{token}")
    fun changePassword(
            @PathVariable("token") token: UUID,
            @Valid @RequestBody data: RecoveryPasswordDto
    ): ResponseEntity<Unit> {
        webService.changePassword(token, data)
        return ResponseEntity<Unit>(HttpStatus.OK)
    }
}