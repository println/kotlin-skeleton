package skeleton.app.support.access.account

import skeleton.app.support.access.account.Permission.*

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
            MANAGER_CREATE))
}