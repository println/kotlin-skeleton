package skeleton.app.replicas.customer.eventsourcing.document


import skeleton.app.configuration.constants.EventSourcingBeanQualifiers
import skeleton.app.replicas.customer.CustomerDoc
import skeleton.app.replicas.customer.CustomerDocService
import skeleton.app.support.eventsourcing.connectors.ConsumerConnector
import skeleton.app.support.eventsourcing.controller.AbstractConsumerController
import skeleton.app.support.eventsourcing.controller.annotations.ConsumptionHandler
import skeleton.app.support.extensions.ClassExtensions.toObject
import gsl.schemas.DocumentExpired
import gsl.schemas.DocumentReleased
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class CustomerDocumentController(
        @Qualifier(EventSourcingBeanQualifiers.CUSTOMER_DOCUMENT_CONSUMER)
        private val consumerConnector: ConsumerConnector,
        private val service: CustomerDocService
) : AbstractConsumerController(consumerConnector) {

    @ConsumptionHandler(DocumentReleased::class)
    fun saveDocument(document: DocumentReleased) {
        val data = document.document.toObject<CustomerDoc>()
        service.save(document.documentId, data)
    }

    @ConsumptionHandler(DocumentExpired::class)
    fun removeDocument(document: DocumentExpired) {
        service.delete(document.documentId)
    }
}
