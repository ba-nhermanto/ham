package com.ham.activitymonitorapp.events

object BatteryEventBus {
    private val listeners = mutableListOf<(BatteryEvent) -> Unit>()

    fun subscribe(listener: (BatteryEvent) -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: (BatteryEvent) -> Unit) {
        listeners.remove(listener)
    }

    fun publish(event: BatteryEvent) {
        listeners.forEach { listener ->
            listener.invoke(event)
        }
    }
}