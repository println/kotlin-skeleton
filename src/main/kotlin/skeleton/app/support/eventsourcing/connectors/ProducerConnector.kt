package skeleton.app.support.eventsourcing.connectors

interface ProducerConnector {
    fun publish(messageJson: String, target: String)
}