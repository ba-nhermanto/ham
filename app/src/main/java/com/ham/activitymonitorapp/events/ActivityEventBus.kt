package com.ham.activitymonitorapp.events

object ActivityEventBus {
    private val listeners = mutableListOf<(ActivityEvent) -> Unit>()

    fun subscribe(listener: (ActivityEvent) -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: (ActivityEvent) -> Unit) {
        listeners.remove(listener)
    }

    fun publish(event: ActivityEvent) {
        listeners.forEach { listener ->
            listener.invoke(event)
        }
    }
}