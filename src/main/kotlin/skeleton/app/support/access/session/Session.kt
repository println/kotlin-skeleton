package skeleton.app.support.access.session

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import skeleton.app.configuration.constants.TableNames
import skeleton.app.support.access.account.Account
import skeleton.app.support.jpa.Auditable
import skeleton.app.support.jpa.AuditableModel
import java.util.*

@Entity
@Table(name = TableNames.Core.SESSION)
data class Session(
        val token: String,
        @Enumerated(EnumType.STRING)
        val tokenType: TokenType = TokenType.BEARER,
        var revoked: Boolean,
        var expired: Boolean,
        val accountId: UUID,
): AuditableModel<Session>()