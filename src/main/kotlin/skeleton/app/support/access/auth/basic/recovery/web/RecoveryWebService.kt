package skeleton.app.support.access.auth.basic.recovery.web

import org.springframework.stereotype.Service
import skeleton.app.support.access.account.Account
import skeleton.app.support.access.account.web.AccountDto
import skeleton.app.support.access.auth.basic.recovery.RecoveryService
import skeleton.app.support.access.auth.basic.recovery.RecoveryToken
import skeleton.app.support.web.AbstractWebService
import java.util.*

@Service
class RecoveryWebService(
        private val service: RecoveryService
) : AbstractWebService<RecoveryToken>() {
    fun forgot(data: RecoveryEmailDto) {
        service.forgot(data.email)
    }

    fun changePassword(tokenId: UUID, data: RecoveryPasswordDto): AccountDto {
        val nullableEntity = service.changePassword(tokenId, data.securityCode, data.password)
        val entity: Account = assertNotFound(nullableEntity)
        return AccountDto(entity)
    }
}