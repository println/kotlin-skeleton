package skeleton.app.support.access.account.web

import skeleton.app.support.access.account.Account

class AccountDto (private val account: Account){
    val id = account.id
    val email = account.email
    val role = account.role
    val isEnabled = account.isEnabled
    val username = account.username
    val lastModified = account.lastModified
    val createdAt = account.createdAt
    val issues = account.issues
}