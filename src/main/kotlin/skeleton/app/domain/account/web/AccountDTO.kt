package skeleton.app.domain.account.web

import skeleton.app.domain.account.Account

class AccountDTO (private val account: Account){
    val email = account.email
    val user = account.user
    val role = account.role
    val isEnabled = account.isEnabled
    val username = account.username
    val authorities = account.authorities
    val isAccountNonLocked = account.isAccountNonLocked
    val isCredentialsNonExpired = account.isCredentialsNonExpired
    val isAccountNonExpired = account.isAccountNonExpired
}