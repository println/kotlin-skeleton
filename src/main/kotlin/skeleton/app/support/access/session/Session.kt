package skeleton.app.support.access.session

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames.Core.SESSION
import skeleton.app.support.access.session.TokenType.*
import skeleton.app.support.jpa.AuditableModel
import java.util.*

@Entity
@Table(name = SESSION)
data class Session(
        val token: String,
        @Enumerated(EnumType.STRING)
        val tokenType: TokenType = BEARER,
        var revoked: Boolean,
        var expired: Boolean,
        val accountId: UUID,
): AuditableModel<Session>()