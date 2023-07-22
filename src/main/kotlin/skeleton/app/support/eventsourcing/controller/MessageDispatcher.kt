package skeleton.app.support.eventsourcing.controller

import skeleton.app.support.eventsourcing.controller.annotations.ConsumptionHandler
import skeleton.app.support.eventsourcing.controller.constants.ErrorType
import skeleton.app.support.eventsourcing.messages.Message
import skeleton.app.support.extensions.ClassExtensions.logger
import skeleton.app.support.extensions.ClassExtensions.toObject
import skeleton.app.support.functions.Functions
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Method

class MessageDispatcher(
        private val instance: Any,
        private val sourceId: String
) {
    private val logger = logger()
    private val errorHandler = MessageErrorDispatcher(instance)
    private val dispatchTable: Map<String, Pair<Class<*>, Method>>


    init {
        dispatchTable = extractDispatchTable()
        if (!canRun()) {
            logger.error("[$sourceId] Dispatcher not has handle methods")
        }
    }

    private fun extractDispatchTable(): Map<String, Pair<Class<*>, Method>> {
        val methods = ReflectionUtils.getAllDeclaredMethods(instance::class.java)
        return methods.filter { it.isAnnotationPresent(ConsumptionHandler::class.java) }
                .filter { it.parameterCount == 1 }
                .associateBy(
                        { Functions.Message.extractIdentifier(it.getAnnotation(ConsumptionHandler::class.java)) },
                        { Pair(it.parameterTypes[0], it) })
    }

    fun dispatch(message: Message): Boolean {
        if (dispatchTable.isEmpty()) {
            logger.error("[$sourceId] Not have handlers")
            errorHandler.dispatchError(ErrorType.EMPTY_HANDLERS, message.identifier, message.content)
            return false
        }

        if (!dispatchTable.containsKey(message.identifier)) {
            logger.error("[$sourceId] Handle Id not found")
            return errorHandler.dispatchError(ErrorType.NOT_FOUND, message.identifier, message.content)
        }

        if (!Functions.Json.isValid(message.content)) {
            logger.error("[$sourceId] Can't parse message content")
            return errorHandler.dispatchError(ErrorType.PARSE_FAILED, message.identifier, message.content)
        }

        val (type, handler) = dispatchTable.getValue(message.identifier)
        val typedMessage = message.content.toObject(type)

        return when (handler.invoke(instance, typedMessage)) {
            true -> true
            false -> false
            null -> true
            else -> true
        }
    }

    fun canRun() = dispatchTable.isNotEmpty()

}