package skeleton.app.core.account.web

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints
import skeleton.app.core.account.Account
import skeleton.app.core.account.AccountService
import skeleton.app.core.user.UserFilter
import java.util.function.Function


@RequestMapping(Endpoints.ACCOUNT, Endpoints.ACCOUNT_)
@RestController
class AccountController(
        private val service: AccountService
) {

    @GetMapping
    fun getAll(
            pageable: Pageable): Page<AccountDTO> {
        val filter = UserFilter()
        val page = service.findAll(filter, pageable)
        val converter = Function<Account?, AccountDTO> {
            AccountDTO(it)
        }
        return page.map(converter);
    }
}