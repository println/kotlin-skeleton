package skeleton.app.support.access.issue.web

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ForgotPasswordSecurityCodeDto(
        @field: Size(min = 4, max = 4)
        @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$")
        val securityCode: String,
)