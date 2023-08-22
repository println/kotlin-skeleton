package skeleton.app.core.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.ResourcePaths
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserRepository
import skeleton.app.domain.user.web.UserController
import skeleton.app.domain.user.web.UserWebService
import skeleton.app.support.access.session.Session
import skeleton.app.support.access.session.SessionRepository
import skeleton.app.support.access.session.web.SessionController
import skeleton.app.support.access.session.web.SessionWebService

class SessionIT: AbstractWebIT<Session>() {
    companion object {
        const val RESOURCE = ResourcePaths.SESSION
    }

    @Autowired
    private lateinit var repository: SessionRepository

    @Autowired
    private lateinit var service: SessionWebService

    override fun getRepository() = repository
    override fun getEntityType() = Session::class.java

    override fun getResource() = RESOURCE

    override fun createResource(): Any {
        return SessionController(service)
    }
}