package skeleton.app.support.access.management

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import skeleton.app.configuration.constants.Endpoints.ADMIN
import skeleton.app.configuration.constants.Endpoints.ADMIN_


@RestController
@RequestMapping(ADMIN, ADMIN_)
@PreAuthorize("hasRole('ADMIN')")
class AdminController {
}