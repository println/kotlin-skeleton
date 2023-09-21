package skeleton.app.support.access

import skeleton.app.support.access.account.Account
import java.util.*

interface AccountUserService {
    fun create(account: Account, firstName: String, lastName: String): AccountUser
    
    fun findByAccount(account: Account): AccountUser

    fun findByAccountId(accountId: UUID): AccountUser

}