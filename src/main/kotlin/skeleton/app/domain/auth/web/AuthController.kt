package skeleton.app.domain.auth.web

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.ServiceNames

@RequestMapping(ServiceNames.USER)
@RestController
class AuthController {
}