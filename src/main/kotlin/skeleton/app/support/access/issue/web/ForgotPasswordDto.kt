package skeleton.app.support.access.issue.web

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.*

data class ForgotPasswordDto(
        val tokenId: UUID,
        @field: Size(min=6, max = 128)
        val password: String,
        @field: Size(min=4, max = 4)
        @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$")
        val securityCode: String
)