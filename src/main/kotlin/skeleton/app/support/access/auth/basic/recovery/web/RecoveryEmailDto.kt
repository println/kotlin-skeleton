package skeleton.app.support.access.auth.basic.recovery.web

import jakarta.validation.constraints.Email

data class RecoveryEmailDto(
        @field: Email
        val email: String,
)