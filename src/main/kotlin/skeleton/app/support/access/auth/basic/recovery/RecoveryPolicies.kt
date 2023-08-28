package skeleton.app.support.access.auth.basic.recovery

import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.Instant
import java.util.*

object RecoveryPolicies {
    fun assertValidToken(tokenOptional: Optional<RecoveryToken>) {
        if (tokenOptional.isEmpty) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session not found")
        }

        val token = tokenOptional.get()
        val duration  = Duration.ofHours(2)
        if (token.recoveryDate.before(Date.from(Instant.now().minus(duration)))) {
            throw ResponseStatusException(BAD_REQUEST, "Password recovery session has been expired!")
        }
    }
}