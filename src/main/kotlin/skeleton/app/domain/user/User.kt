package skeleton.app.domain.user

import skeleton.app.configuration.constants.TableNames
import skeleton.app.support.jpa.AuditableModel
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(name = TableNames.Domain.USER)
data class User(
        val customerId: UUID,
        val pickupAddress: String,
        val deliveryAddress: String,
        var value: BigDecimal? = null,
        var comment: String = "",
        @Enumerated(EnumType.STRING)
        var status: UserStatus = UserStatus.WAITING_PAYMENT,
) : AuditableModel<User>()
