package skeleton.app.support.access.auth.basic.auth.web


import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.support.access.auth.basic.auth.AuthRegisterRequest
import skeleton.app.support.access.auth.basic.auth.AuthRequest
import skeleton.app.support.access.auth.basic.auth.AuthTokens


@RestController
@RequestMapping(Endpoints.AUTH, Endpoints.AUTH_)
class AuthController(
        private val authWebService: AuthWebService
) {

    @PostMapping("/register")
    fun register(
            @Valid @RequestBody request: AuthRegisterRequest
    ): ResponseEntity<AuthTokens> {
        val token = authWebService.register(request)
        return ResponseEntity((token), HttpStatus.CREATED)
    }

    @PostMapping("/authenticate")
    fun authenticate(
            @RequestBody request: AuthRequest
    ): ResponseEntity<AuthTokens> {
        val token = authWebService.authenticate(request)
        return ResponseEntity.ok(token)
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
            request: HttpServletRequest,
            response: HttpServletResponse
    ) {
        val tokens = authWebService.refreshToken(request, response)
        ObjectMapper().writeValue(response.outputStream, tokens)
    }

}