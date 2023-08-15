package skeleton.app.domain.user

import skeleton.app.configuration.constants.TableNames
import skeleton.app.support.jpa.AuditableModel
import jakarta.persistence.*

@Entity
@Table(name = TableNames.System.USER)
data class User(
        val firstName: String,
        val lastName: String,
) : AuditableModel<User>()
