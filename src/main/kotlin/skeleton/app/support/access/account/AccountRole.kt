package skeleton.app.support.access.account

import org.springframework.security.core.authority.SimpleGrantedAuthority
import skeleton.app.support.access.account.Permission.*
import java.util.stream.Collectors


enum class AccountRole(
        val permissions: Set<Permission>
) {
    USER(setOf()),

    ADMIN(setOf(
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            ADMIN_CREATE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_DELETE,
            MANAGER_CREATE)),

    MANAGER(setOf(
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_DELETE,
            MANAGER_CREATE));

    fun getAuthorities(): List<SimpleGrantedAuthority> {
        val authorities = this.permissions
                .stream()
                .map { permission -> SimpleGrantedAuthority(permission.permission) }
                .collect(Collectors.toList())
        authorities.add(SimpleGrantedAuthority("ROLE_$name"))
        return authorities
    }
}