package skeleton.app.support.web

import org.springframework.http.HttpStatus.*
import org.springframework.web.server.ResponseStatusException

abstract class AbstractWebService<T> {
    fun <T> assertBadRequest(nullableEntity: T?): T {
        return nullableEntity ?: throw ResponseStatusException(BAD_REQUEST, "Wrong Request")
    }

    fun <T> assertNotFound(nullableEntity: T?): T {
        return nullableEntity ?: throw ResponseStatusException(NOT_FOUND, "Not found")
    }
}