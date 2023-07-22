package skeleton.app.support.eventsourcing.connectors.amqp

import skeleton.app.support.eventsourcing.connectors.AbstractConnectorFactory
import skeleton.app.support.eventsourcing.connectors.ConsumerConnector
import skeleton.app.support.eventsourcing.connectors.ProducerConnector
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.MessageConverter

class AmqpConnectorFactory(private val connectionFactory: ConnectionFactory,
                           private val messageConverter: MessageConverter) : AbstractConnectorFactory() {

    override fun createProducer(): ProducerConnector {
        return AmqpProducerConnector(connectionFactory, messageConverter)
    }

    override fun createConsumer(target: String): ConsumerConnector {
        return AmqpConsumerConnector(target, connectionFactory)
    }
}