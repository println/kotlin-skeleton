package skeleton.app.support.access.account


import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import skeleton.app.configuration.constants.TableNames
import skeleton.app.domain.user.User
import skeleton.app.support.access.auth.basic.login.Login
import skeleton.app.support.jpa.Auditable
import skeleton.app.support.jpa.AuditableModel

@Entity
@Table(name = TableNames.Core.ACCOUNT)
data class Account(
        var firstName: String,
        var lastName: String,
        @Column(unique=true, nullable = false)
        var email: String,
        @OneToOne(
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.LAZY)
        val login: Login,
        @Enumerated(EnumType.STRING)
        var role: AccountRole = AccountRole.USER
): AuditableModel<Account>(), UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(this.role.name))
    }

    override fun getPassword(): String {
        return login.password
    }

    override fun getUsername(): String {
        return login.username
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