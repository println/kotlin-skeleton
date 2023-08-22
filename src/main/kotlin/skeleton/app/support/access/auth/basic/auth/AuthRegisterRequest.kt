package skeleton.app.support.access.auth.basic.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size


data class AuthRegisterRequest(
        @field: NotEmpty
        val firstName: String,
        @field: NotEmpty
        val lastName: String,
        @field: Email
        val email: String,
        @field: Size(min=6, max = 128)
        val password: String,
)