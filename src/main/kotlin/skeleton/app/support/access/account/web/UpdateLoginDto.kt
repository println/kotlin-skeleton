package skeleton.app.support.access.account.web

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class UpdateLoginDto(
        @field: Email
        val email: String,
        @field: Size(min = 6, max = 128)
        val password: String,
)
