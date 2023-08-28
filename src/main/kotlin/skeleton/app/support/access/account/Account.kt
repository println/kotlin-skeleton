package skeleton.app.support.access.account


import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import skeleton.app.configuration.constants.TableNames.Core.ACCOUNT
import skeleton.app.support.access.account.AccountRole.*
import skeleton.app.support.access.account.AccountStatus.*
import skeleton.app.support.access.login.Login
import skeleton.app.support.jpa.AuditableModel

@Entity
@Table(name = ACCOUNT)
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
        var role: AccountRole = USER,
        @Enumerated(EnumType.STRING)
        var status: AccountStatus = ENABLED
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
        return status != EXPIRED
    }

    override fun isAccountNonLocked(): Boolean {
        return status != LOCKED
    }

    override fun isCredentialsNonExpired(): Boolean {
        return status != CREDENTIALS_EXPIRED
    }

    override fun isEnabled(): Boolean {
        return status == ENABLED
    }
}