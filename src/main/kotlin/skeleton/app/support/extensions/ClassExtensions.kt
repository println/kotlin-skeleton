package skeleton.app.support.extensions

import com.fasterxml.jackson.annotation.JsonInclude.Include.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ClassExtensions {
    val mapper: ObjectMapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .setSerializationInclusion(NON_EMPTY)
            .setSerializationInclusion(NON_NULL)

    fun <R : Any> R.logger(): Logger {
        return LoggerFactory.getLogger((this::class).java.name)
    }

    fun <R : Any> R.toJsonString(): String {
        return mapper.writeValueAsString(this)
    }

    fun <R : Any> String.toObject(clazz: Class<R>): R {
        return mapper.readValue(this, clazz)
    }

    inline fun <reified R : Any> String.toObject(): R {
        return mapper.readValue(this, R::class.java)
    }
}