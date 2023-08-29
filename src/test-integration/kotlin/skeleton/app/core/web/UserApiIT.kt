package skeleton.app.core.web

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import skeleton.app.AbstractWebIT
import skeleton.app.configuration.constants.Endpoints.USER
import skeleton.app.domain.user.User
import skeleton.app.domain.user.UserRepository
import skeleton.app.domain.user.web.UserController
import skeleton.app.domain.user.web.UserDto
import skeleton.app.domain.user.web.UserWebService
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import java.util.*

class UserApiIT : AbstractWebIT<User>() {

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
