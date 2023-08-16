package skeleton.app.core.user

import jakarta.persistence.Entity
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames
import skeleton.app.support.jpa.AuditableModel

@Entity
@Table(name = TableNames.Core.USER)
data class User(
        val firstName: String,
        val lastName: String,
) : AuditableModel<User>()
