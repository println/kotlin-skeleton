package skeleton.app.support.access.auth.basic.auth

import jakarta.validation.constraints.Email

data class AuthRequest(
        @field: Email
        val email: String,
        val password: String
)
