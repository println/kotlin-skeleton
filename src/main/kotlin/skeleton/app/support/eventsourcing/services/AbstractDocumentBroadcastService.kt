package skeleton.app.support.eventsourcing.services

import skeleton.app.support.eventsourcing.connectors.DedicatedProducerConnector
import skeleton.app.support.eventsourcing.messages.DocumentMessage
import skeleton.app.support.extensions.ClassExtensions.logger
import skeleton.app.support.extensions.ClassExtensions.toJsonString
import gsl.schemas.DocumentExpired
import gsl.schemas.DocumentReleased
import java.time.Instant
import java.util.*

abstract class AbstractDocumentBroadcastService<T>(
        private val dedicatedProducerConnector: DedicatedProducerConnector
) {

    protected val logger = logger()

    abstract fun release(entity: T)

    protected fun broadcastRelease(documentId: UUID, trackId: UUID, documentJson: String) {
        val document = DocumentReleased(documentId, documentJson, Instant.now())
        val message = DocumentMessage(trackId, document)
        notify(message)
    }

    fun notifyExpiration(trackId: UUID, documentId: UUID) {
        val document = DocumentExpired(documentId, Instant.now())
        val message = DocumentMessage(trackId, document)
        notify(message)
    }

    private fun notify(message: DocumentMessage) {
        dedicatedProducerConnector.publish(message)
        logger.info("Document has been spread to [${dedicatedProducerConnector.getId()}]: ${message.toJsonString()}")
    }
}