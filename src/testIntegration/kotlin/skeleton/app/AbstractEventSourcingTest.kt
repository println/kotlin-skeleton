package skeleton.app

import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import skeleton.app.support.extensions.ClassExtensions.toObject
import skeleton.app.support.jpa.AuditableModel
import skeleton.app.support.web.ResponsePage

abstract class AbstractEventSourcingTest : AbstractIntegrationTest() {
    @AfterEach
    fun resetEventSourcing() {
    }

    protected fun assertTotalMessagesAndReleaseThem(total: Long = 1) {
    }

    protected final inline fun <reified T : AuditableModel<T>> assertDocumentReleased(entity: T) {

    }

    protected final inline fun <reified T : Any> checkAllFromApiAndGetFirst(resource: String, size: Int = 1): T {
        val result = restMockMvc.perform(MockMvcRequestBuilders.get(resource)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content", Matchers.hasSize<String>(size)))
                .andReturn()

        val response = result.response
        val page = response.contentAsString.toObject<ResponsePage>()
        return page.getObject<T>()[0]
    }
}