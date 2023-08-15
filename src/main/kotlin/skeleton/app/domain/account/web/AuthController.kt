package skeleton.app.domain.account.web


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints


@RestController
@RequestMapping(Endpoints.AUTH, Endpoints.AUTH_)
class AuthController(
        private val authWebService: AuthWebService
) {

    @PostMapping("/register")
    fun register(
            @RequestBody request: RegisterRequestDTO
    ): ResponseEntity<AuthenticationResponseDTO> {
        val account = authWebService.register(request)
        val token = authWebService.generateToken(account)
        return ResponseEntity((token), HttpStatus.CREATED)
    }

    @PostMapping("/authenticate")
    fun authenticate(
            @RequestBody request: AuthenticationRequestDTO
    ): ResponseEntity<AuthenticationResponseDTO> {
        val account = authWebService.authenticate(request)
        val token = authWebService.generateToken(account)
        return ResponseEntity.ok(token)
    }

}