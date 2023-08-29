package skeleton.app.api.domain

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.Endpoints.USER
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserRepository
import skeleton.app.domain.user.web.UserController
import skeleton.app.domain.user.web.UserWebService

class UserIT : AbstractWebIT<User>() {

    companion object {
        const val RESOURCE = USER
    }

    @Autowired
    private lateinit var repository: UserRepository

    @Autowired
    private lateinit var service: UserWebService

    override fun getRepository() = repository
    override fun getEntityType() = User::class.java

    override fun getResource() = RESOURCE

    override fun createResource(): Any {
        return UserController(service)
    }

    @AfterEach
    fun reset() {
        repository.deleteAll()
    }
}
