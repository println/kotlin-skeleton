package skeleton.app.domain.user

import jakarta.persistence.Entity
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames.Core.USER
import skeleton.app.support.access.AccountUser
import skeleton.app.support.jpa.AuditableModel
import java.util.*

@Entity
@Table(name = USER)
data class User(
        override val firstName: String,
        override val lastName: String,
        val accountId: UUID
) : AuditableModel<User>(), AccountUser
