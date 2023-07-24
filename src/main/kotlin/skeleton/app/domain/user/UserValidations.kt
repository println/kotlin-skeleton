package skeleton.app.domain.user

import skeleton.app.domain.user.UserStatus.ACCEPTED
import skeleton.app.domain.user.UserStatus.REFUSED
import java.math.BigDecimal


object UserValidations {
    fun canApprove(value: BigDecimal, entity: Userr?) =
            (entity != null)
                    .and(value > BigDecimal.ZERO)
                    .and(entity!!.status.canChangeTo(ACCEPTED))


    fun canRefuse(reason: String, entity: Userr?) =
            (entity != null)
                    .and(reason.isNotBlank())
                    .and(entity!!.status.canChangeTo(REFUSED))
}