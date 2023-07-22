package skeleton.app.support.eventsourcing.connectors

import skeleton.app.support.eventsourcing.messages.Message
import skeleton.app.support.extensions.ClassExtensions.logger
import skeleton.app.support.extensions.ClassExtensions.toJsonString

class DedicatedProducerConnector(
        private val producerConnector: ProducerConnector,
        private val target: String) {

    private val logger = logger()

    init {
        logger.info("Creating Dedicated Producer: $target")
    }

    fun publish(message: Message) {
        producerConnector.publish(message.toJsonString(), target)
    }

    fun getId(): String {
        return target
    }
}