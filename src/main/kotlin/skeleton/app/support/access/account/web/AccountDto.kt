package skeleton.app.support.access.account.web

import skeleton.app.support.access.account.Account

class AccountDto (private val account: Account){
    val id = account.id
    val firstName = account.firstName
    val lastName = account.lastName
    val email = account.email
    val role = account.role
    val isEnabled = account.isEnabled
    val username = account.username
    val authorities = account.authorities
    val isAccountNonLocked = account.isAccountNonLocked
    val isCredentialsNonExpired = account.isCredentialsNonExpired
    val isAccountNonExpired = account.isAccountNonExpired
    val lastModified = account.lastModified
    val createdAt = account.createdAt
}