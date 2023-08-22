package skeleton.app.support.access.auth.basic.recovery

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RecoveryRepository : JpaRepository<RecoveryToken, UUID>, JpaSpecificationExecutor<RecoveryToken> {
    fun findByIdAndSecurityCodeAndStatus(tokenId: UUID,
                                         securityCode: String,
                                         status: RecoveryTokenStatus = RecoveryTokenStatus.OPEN): Optional<RecoveryToken>

    fun existsByAccountId(accountId: UUID): Boolean
}