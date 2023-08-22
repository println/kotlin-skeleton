package skeleton.app.support.access.auth.basic.recovery.web

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RecoveryPasswordDto(
        @field: Size(min=6, max = 128)
        val password: String,
        @field: Size(min=4, max = 4)
        @Pattern(regexp = "^[0-9]+(\\.[0-9]+)?$")
        val securityCode: String
)