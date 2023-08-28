package skeleton.app

import org.hamcrest.Matchers.*
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.MediaType.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import skeleton.app.support.jpa.AuditableModel
import java.util.*

abstract class AbstractWebIT<T : AuditableModel<T>> : AbstractIT() {
    val easyRandom = EasyRandom()
    var entities = listOf<AuditableModel<T>>()

    abstract fun getRepository(): JpaRepository<T, *>
    abstract fun getEntityType(): Class<T>
    open fun preProcessing(data: T) = data

    abstract fun getResource(): String

    @BeforeEach
    fun setupWeb() {
        reloadData { preProcessing(it) }
    }

    fun reloadData(adjustment: (T) -> T) {
        val data = easyRandom.objects(getEntityType(), 200).toList()
        val processedData = data.map { adjustment(it) }
        getRepository().deleteAll()
        entities = getRepository().saveAllAndFlush(processedData)
    }

    @ParameterizedTest
    @CsvSource(
            "'', true, 0, false",
            "?page=1, true, 0, false",
            "?page=2, false, 1, false",
            "?page=10, false, 9, true"
    )
    fun getAll(page: String, first: Boolean, index: Int, last: Boolean) {
        restMockMvc.perform(get("${getResource()}$page")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("\$.content", hasSize<String>(20)))
                .andExpect(jsonPath("\$.first").value(first))
                .andExpect(jsonPath("\$.number").value(index))
                .andExpect(jsonPath("\$.last").value(last))
                .andExpect(jsonPath("\$.totalPages").value(10))
                .andExpect(jsonPath("\$.numberOfElements").value(20))
                .andExpect(jsonPath("\$.size").value(20))
    }

    @Test
    fun getById() {
        val id = entities.first().id
        restMockMvc.perform(get("${getResource()}/{id}", id)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("\$").isNotEmpty)
    }

    @Test
    fun getByWrongIdNotFound() {
        val id = UUID.randomUUID()
        restMockMvc.perform(get("${getResource()}/{id}", id)
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound)
    }
}