package skeleton.app

import org.junit.jupiter.api.BeforeEach
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder


abstract class AbstractIT: IntegrationTest() {

    @Autowired
    lateinit var pageable: PageableHandlerMethodArgumentResolver

    protected lateinit var restMockMvc: MockMvc

    @BeforeEach
    fun init() {
        MockitoAnnotations.openMocks(this)
        this.restMockMvc = this.getMvcBuilder(this.createResource()).build()
    }

    abstract fun createResource(): Any

    fun getMvcBuilder(resource: Any): StandaloneMockMvcBuilder {
        return MockMvcBuilders
                .standaloneSetup(resource)
                .apply { springSecurity() }
                .setCustomArgumentResolvers(pageable)
    }
}