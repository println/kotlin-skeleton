package skeleton.app.support.access.management

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints


@RequestMapping(Endpoints.MANAGEMENT, Endpoints.MANAGEMENT_)
@RestController
class ManagementController {
    fun createAccount(){}
    fun activeAccount(){}

    fun disableAccount(){}

    fun removeAccount(){}

    fun changePasswordAccount(){}
}