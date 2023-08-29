package skeleton.app.support.access

import skeleton.app.support.access.account.Account

interface AccountUserService {
    fun create(account: Account, firstName: String, lastName: String): AccountUser
    
    fun findByAccount(account: Account): AccountUser

}