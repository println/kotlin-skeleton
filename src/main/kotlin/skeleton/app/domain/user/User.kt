package skeleton.app.domain.user

import jakarta.persistence.Entity
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames.Core.USER
import skeleton.app.support.jpa.AuditableModel

@Entity
@Table(name = USER)
data class User(
        val firstName: String,
        val lastName: String,
) : AuditableModel<User>()
