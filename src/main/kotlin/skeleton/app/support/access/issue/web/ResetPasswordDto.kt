package skeleton.app.support.access.issue.web

import jakarta.validation.constraints.Size

data class ResetPasswordDto(
        @field: Size(min = 6, max = 128)
        val password: String
)