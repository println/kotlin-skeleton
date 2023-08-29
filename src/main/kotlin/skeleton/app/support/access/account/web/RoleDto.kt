package skeleton.app.support.access.account.web

import jakarta.validation.constraints.NotEmpty
import skeleton.app.support.access.account.AccountRole

data class RoleDto(
        @field: NotEmpty
        val role: AccountRole
)