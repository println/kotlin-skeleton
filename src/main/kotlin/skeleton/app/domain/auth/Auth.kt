package skeleton.app.domain.auth

import skeleton.app.configuration.constants.TableNames
import skeleton.app.support.jpa.AuditableModel
import java.math.BigDecimal
import java.util.*
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = TableNames.Domain.USER)
data class Auth(
        val customerId: UUID,
        val pickupAddress: String,
        val deliveryAddress: String,
        var value: BigDecimal? = null,
        var comment: String = "",
        @Enumerated(EnumType.STRING)
        var status: AuthStatus = AuthStatus.WAITING_PAYMENT
) : AuditableModel<Auth>()
