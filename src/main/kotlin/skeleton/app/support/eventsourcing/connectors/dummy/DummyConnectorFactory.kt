package skeleton.app.support.eventsourcing.connectors.dummy

import skeleton.app.support.eventsourcing.connectors.AbstractConnectorFactory
import skeleton.app.support.eventsourcing.connectors.ConsumerConnector
import skeleton.app.support.eventsourcing.connectors.ProducerConnector


class DummyConnectorFactory : AbstractConnectorFactory() {

    override fun createProducer(): ProducerConnector {
        return DummyProducerConnector()
    }

    override fun createConsumer(target: String): ConsumerConnector {
        return DummyConsumerConnector(target)
    }
}