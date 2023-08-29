package skeleton.app.support.access.issue

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames.Core.RECOVERY
import skeleton.app.support.access.issue.IssueStatus.*
import skeleton.app.support.jpa.AuditableModel
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = RECOVERY)
class IssueToken(
        val accountId: UUID,
        val securityCode: String,
        val recoveryExpiration: LocalDateTime = LocalDateTime.now(),
        @Enumerated(EnumType.STRING)
        val type: IssueType,
        @Enumerated(EnumType.STRING)
        var status: IssueStatus = OPEN
): AuditableModel<IssueToken>()