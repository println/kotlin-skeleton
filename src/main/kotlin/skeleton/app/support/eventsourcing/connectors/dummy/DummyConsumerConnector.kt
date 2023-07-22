package skeleton.app.support.eventsourcing.connectors.dummy

import skeleton.app.support.eventsourcing.connectors.AbstractConsumerConnector
import skeleton.app.support.extensions.ClassExtensions.logger


class DummyConsumerConnector(
        val target: String) : AbstractConsumerConnector() {

    private val logger = logger()

    init {
        logger.info("Creating Dummy Consumer Connector: $target")
        DummyProducerConnector.consumers.add(this)
    }

    override fun startConsumer() {
    }

    override fun stopConsumer() {
    }

    override fun getId(): String {
        return target
    }

}