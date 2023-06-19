package com.ham.activitymonitorapp.events

object ActiveUserEventBus {
    private val listeners = mutableListOf<(ActiveUserChangeEvent) -> Unit>()

    fun subscribe(listener: (ActiveUserChangeEvent) -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: (ActiveUserChangeEvent) -> Unit) {
        listeners.remove(listener)
    }

    fun publish(event: ActiveUserChangeEvent) {
        listeners.forEach { listener ->
            listener.invoke(event)
        }
    }
}