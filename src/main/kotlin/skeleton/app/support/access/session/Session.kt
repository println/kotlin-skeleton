package skeleton.app.support.access.session

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import skeleton.app.configuration.constants.TableNames.Core.SESSION
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.session.TokenType.*
import skeleton.app.support.jpa.AuditableModel

@Entity
@Table(name = SESSION)
data class Session(
        val token: String,
        @Enumerated(EnumType.STRING)
        val tokenType: TokenType = BEARER,
        var revoked: Boolean,
        var expired: Boolean,
        @ManyToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        val account: Account,
): AuditableModel<Session>()