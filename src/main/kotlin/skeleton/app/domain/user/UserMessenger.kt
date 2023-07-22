package skeleton.app.domain.user

import skeleton.app.domain.user.eventsourcing.command.FreightCommandService
import skeleton.app.domain.user.eventsourcing.document.OrderDocumentBroadcastService
import skeleton.app.domain.user.eventsourcing.event.OrderEventService
import org.springframework.stereotype.Component

@Component
class UserMessenger(
        private val freightCommandService: FreightCommandService,
        private val orderEventService: OrderEventService,
        private val orderDocumentBroadcastService: OrderDocumentBroadcastService) {

    fun create(entity: User) {
        orderEventService.notifyOrderCreated(entity.id!!)
        orderDocumentBroadcastService.release(entity)
    }

    fun approvePayment(entity: User) {
        val orderId = entity.id!!
        orderEventService.notifyOrderAccepted(orderId)
        freightCommandService.create(orderId, orderId, entity.pickupAddress, entity.deliveryAddress)
        orderDocumentBroadcastService.release(entity)
    }

    fun refusePayment(entity: User) {
        orderEventService.notifyOrderRefused(entity.id!!)
        orderDocumentBroadcastService.release(entity)
    }
}