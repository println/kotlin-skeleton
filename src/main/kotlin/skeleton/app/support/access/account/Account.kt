package skeleton.app.support.access.account


import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import skeleton.app.configuration.constants.TableNames.Core.ACCOUNT
import skeleton.app.support.access.account.AccountRole.*
import skeleton.app.support.access.account.AccountStatus.*
import skeleton.app.support.access.login.Login
import skeleton.app.support.jpa.AuditableModel

@Entity
@Table(name = ACCOUNT)
data class Account(
        @Column(nullable = false)
        var name: String,
        @Column(unique = true, nullable = false)
        var email: String,
        @OneToOne(
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.LAZY)
        val login: Login,
        @Enumerated(EnumType.STRING)
        var role: AccountRole = USER,
        @Enumerated(EnumType.STRING)
        var status: AccountStatus = ACTIVE,
        var issues: Int = 0
) : AuditableModel<Account>(), UserDetails {
    override fun getAuthorities(): List<GrantedAuthority> {
        return role.getAuthorities()
    }

    override fun getPassword(): String {
        return login.password
    }

    override fun getUsername(): String {
        return login.username
    }

    override fun isAccountNonExpired(): Boolean {
        return status != BLOCKED
    }

    override fun isAccountNonLocked(): Boolean {
        return status != BLOCKED
    }

    override fun isCredentialsNonExpired(): Boolean {
        return status != BLOCKED
    }

    override fun isEnabled(): Boolean {
        return status == ACTIVE
    }
}