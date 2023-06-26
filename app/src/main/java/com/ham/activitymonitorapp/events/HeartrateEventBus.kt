package com.ham.activitymonitorapp.events

object HeartrateEventBus {
    private val listeners = mutableListOf<(HeartrateEvent) -> Unit>()

    fun subscribe(listener: (HeartrateEvent) -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: (HeartrateEvent) -> Unit) {
        listeners.removeAll(listeners)
    }

    fun publish(event: HeartrateEvent) {
        listeners.forEach { listener ->
            listener.invoke(event)
        }
    }
}