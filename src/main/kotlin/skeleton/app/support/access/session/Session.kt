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
        @ManyToOne
        @OnDelete(action = OnDeleteAction.CASCADE)
        val account: Account,
        @Enumerated(EnumType.STRING)
        val tokenType: TokenType = BEARER,
        var revoked: Boolean = false,
        var expired: Boolean = false,
        ) : AuditableModel<Session>()