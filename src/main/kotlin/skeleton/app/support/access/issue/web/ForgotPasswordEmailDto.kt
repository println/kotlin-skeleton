package skeleton.app.support.access.issue.web

import jakarta.validation.constraints.Email

data class ForgotPasswordEmailDto(
        @field: Email
        val email: String,
)