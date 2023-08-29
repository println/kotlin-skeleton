package skeleton.app.support.access.issue.web

import jakarta.validation.constraints.Size
import java.util.*

data class ResetPasswordDto(
        val tokenId: UUID,
        @field: Size(min = 6, max = 128)
        val password: String
)