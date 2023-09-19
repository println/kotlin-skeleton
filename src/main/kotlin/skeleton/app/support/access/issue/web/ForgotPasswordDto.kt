package skeleton.app.support.access.issue.web

import jakarta.validation.constraints.Size
import java.util.*

data class ForgotPasswordDto(
        val token: UUID,
        @field: Size(min=6, max = 128)
        val password: String
)