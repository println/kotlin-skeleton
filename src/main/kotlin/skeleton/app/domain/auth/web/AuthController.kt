package skeleton.app.domain.auth.web

import skeleton.app.domain.user.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import skeleton.app.configuration.constants.ServiceNames
import skeleton.app.domain.user.web.PaymentDto
import skeleton.app.domain.user.web.UserWebService
import java.util.*


@RequestMapping(ServiceNames.AUTH)
@RestController
class AuthController(
        val service: UserWebService,
        val tokenProvider: TokenProvider
) {
    @PostMapping("/authenticate")
    fun authorize(
            @RequestBody auth: AuthDto): ResponseEntity<*> {
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/signup")
    fun registerUser(
            @RequestBody data: SignupDto): ResponseEntity<*> {
        return ResponseEntity.ok("User registered successfully!");
    }

    @PutMapping("/{id}/approve")
    fun tempApproveOrder(
            @PathVariable("id") id: UUID,
            @RequestBody data: PaymentDto): User {
        return service.approvePayment(id, data.value)
    }

    @PutMapping("/{id}/refuse")
    fun tempRefuseOrder(
            @PathVariable("id") id: UUID): User {
        return service.refusePayment(id, "cant pay")
    }
}