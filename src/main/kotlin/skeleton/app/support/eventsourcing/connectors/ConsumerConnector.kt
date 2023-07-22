package skeleton.app.support.eventsourcing.connectors

import skeleton.app.support.eventsourcing.messages.Message
import org.springframework.beans.factory.DisposableBean

interface ConsumerConnector : DisposableBean {
    fun consume(message: Message)
    fun setDispatcher(handler: (message: Message) -> Boolean)
    fun hasDispatcher(): Boolean
    fun start(): Unit
    fun stop(): Unit
    fun getId(): String
}