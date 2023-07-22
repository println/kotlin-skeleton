package skeleton.app.domain.user.web

import java.util.*

data class UserDto(
        val customerId: UUID,
        val pickupAddress: String,
        val deliveryAddress: String
)
