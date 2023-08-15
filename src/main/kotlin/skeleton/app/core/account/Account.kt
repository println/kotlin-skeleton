package skeleton.app.core.account


import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import skeleton.app.configuration.constants.TableNames
import skeleton.app.core.user.User

@Entity
@Table(name = TableNames.Core.ACCOUNT)
data class Account(
        @Id
        val email: String,
        val pass: String,
        @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        var user: User?,
        @Enumerated(EnumType.STRING)
        var role: AccountRole = AccountRole.USER
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(this.role.name))
    }

    override fun getPassword(): String {
        return pass
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}