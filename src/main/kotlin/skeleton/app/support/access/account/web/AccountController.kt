package skeleton.app.support.access.account.web

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.domain.user.UserFilter
import java.net.URI
import java.util.UUID


@RequestMapping(Endpoints.ACCOUNT, Endpoints.ACCOUNT_)
@RestController
class AccountController(
        private val service: AccountWebService
) {

    @GetMapping
    fun getAll(
            pageable: Pageable): Page<AccountDto> {
        val filter = UserFilter()
        return service.findAll(filter, pageable)
    }

    @GetMapping("/{id}")
    fun getById(
            @PathVariable("id") id: UUID): AccountDto {
        return service.findById(id)
    }

    @PostMapping
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

    @PutMapping("/{id}")
    fun updateInfo(
            @PathVariable("id") id: UUID,
            @Valid @RequestBody updateInfo: UpdateInfoDto): AccountDto {
        return service.updateInfo(id, updateInfo)
    }

    @PutMapping("/{id}/login")
    fun updateCredentials(
            @PathVariable("id") id: UUID,
            @Valid @RequestBody updateLogin: UpdateLoginDto): AccountDto {
        return service.updateCredentials(id, updateLogin)
    }

}