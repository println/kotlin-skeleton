package skeleton.app.support.access.account

class AccountDto (private val account: Account){
    val id = account.id
    val email = account.email
    val name = account.name
    val role = account.role
    val status = account.status
    val isEnabled = account.isEnabled
    val username = account.username
    val lastModified = account.lastModified
    val createdAt = account.createdAt
    val issues = account.issues
}