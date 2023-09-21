package skeleton.app.support.access.account.web

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import skeleton.app.configuration.constants.Endpoints.ACCOUNT
import skeleton.app.configuration.constants.Endpoints.ACCOUNT_
import skeleton.app.support.access.AccountUser
import skeleton.app.support.access.account.AccountDto
import skeleton.app.support.access.account.AccountFilter
import skeleton.app.support.access.account.AccountRole
import skeleton.app.support.access.issue.IssueToken
import skeleton.app.support.access.session.SessionDto
import java.net.URI
import java.util.*


@RequestMapping(ACCOUNT, ACCOUNT_)
@RestController
@PreAuthorize("hasRole('ADMIN')")
class AccountController(
        private val service: AccountWebService
) {

    @GetMapping
    @PreAuthorize("hasAuthority('admin:read')")
    fun getAll(
            pageable: Pageable,
          ): Page<AccountDto> {
        val filter = AccountFilter()
        return service.findAll(filter, pageable)
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('admin:read')")
    fun getEnumRoles(): Array<AccountRole> {
        return AccountRole.values()
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:read')")
    fun getById(
            @PathVariable("id") id: UUID): AccountDto {
        return service.findById(id)
    }

    @GetMapping("/{id}/sessions")
    @PreAuthorize("hasAuthority('admin:read')")
    fun getActiveSessions(
            @PathVariable("id") id: UUID): Collection<SessionDto> {
        return service.getActiveSessions(id)
    }

    @GetMapping("/{id}/issues")
    @PreAuthorize("hasAuthority('admin:read')")
    fun getOpenIssues(
            @PathVariable("id") id: UUID): Collection<IssueToken> {
        return service.getOpenIssues(id)
    }

    @GetMapping("/{id}/user")
    @PreAuthorize("hasAuthority('admin:read')")
    fun getUser(
            @PathVariable("id") id: UUID): AccountUser {
        return service.getUser(id)
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    fun register(
            @Valid @RequestBody register: AccountRegisterDto
    ): ResponseEntity<AccountDto> {
        val entity = service.register(register)

        val location: URI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(entity.id).toUri()

        return ResponseEntity.created(location).body(entity)
    }

    @PutMapping("/{id}/login")
    @PreAuthorize("hasAuthority('admin:update')")
    fun updateCredentials(
            @PathVariable("id") id: UUID,
            @Valid @RequestBody updateLogin: UpdateLoginDto): AccountDto {
        return service.updateCredentials(id, updateLogin)
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('admin:update')")
    fun changeRole(
            @PathVariable("id") id: UUID,
            @Valid @RequestBody role: RoleDto): AccountDto {
        return service.changeRole(id, role.role)
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasAuthority('admin:update')")
    fun blockAccount(
            @PathVariable("id") id: UUID): AccountDto {
        return service.block(id)
    }

    @DeleteMapping("/{id}/block")
    @PreAuthorize("hasAuthority('admin:update')")
    fun unblockAccount(
            @PathVariable("id") id: UUID): AccountDto {
        return service.unblock(id)
    }
}