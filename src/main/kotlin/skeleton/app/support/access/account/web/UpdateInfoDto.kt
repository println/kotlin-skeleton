package skeleton.app.support.access.account.web

import jakarta.validation.constraints.NotEmpty

data class UpdateInfoDto(
        @field: NotEmpty
        val firstName: String,
        @field: NotEmpty
        val lastName: String,
)
