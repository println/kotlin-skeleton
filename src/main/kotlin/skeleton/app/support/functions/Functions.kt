package skeleton.app.support.functions

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature.*
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


object Functions {
    object Json {
        private val mapper: ObjectMapper = jacksonObjectMapper()
                .registerModule((KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(NullToEmptyCollection, false)
                .configure(NullToEmptyMap, false)
                .configure(NullIsSameAsDefault, false)
                .configure(SingletonSupport, false)
                .configure(StrictNullChecks, false)
                .build()))

        fun isValid(json: String): Boolean {
            try {
                mapper.readTree(json)
            } catch (e: JsonProcessingException) {
                return false
            }
            return true
        }
    }

    object Text{
        fun cleaner(word: String) = word.lowercase().trim()
    }
}