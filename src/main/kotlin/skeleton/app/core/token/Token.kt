package skeleton.app.core.token

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import skeleton.app.configuration.constants.TableNames
import skeleton.app.core.account.Account
import java.util.*

@Entity
@Table(name = TableNames.Core.TOKEN)
data class Token(
        @Id
        @UuidGenerator
        var id: UUID? = null,
        @Column(unique = true)
        val token: String,
        @Enumerated(EnumType.STRING)
        val tokenType: TokenType = TokenType.BEARER,
        var revoked: Boolean,
        var expired: Boolean,
        @ManyToOne(fetch = FetchType.LAZY)
        val account: Account,
)