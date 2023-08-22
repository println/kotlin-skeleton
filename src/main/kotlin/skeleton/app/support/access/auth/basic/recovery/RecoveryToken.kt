package skeleton.app.support.access.auth.basic.recovery

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import skeleton.app.configuration.constants.TableNames
import skeleton.app.support.jpa.AuditableModel
import java.util.*

@Entity
@Table(name = TableNames.Core.RECOVERY)
class RecoveryToken(
        val accountId: UUID,
        val securityCode: String,
        val recoveryDate: Date = Date(),
        @Enumerated(EnumType.STRING)
        var status: RecoveryTokenStatus = RecoveryTokenStatus.OPEN
): AuditableModel<RecoveryToken>()