package com.ham.activitymonitorapp.events

object ExerciseEventBus {
    private val listeners = mutableListOf<(ExerciseEvent) -> Unit>()

    fun subscribe(listener: (ExerciseEvent) -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: (ExerciseEvent) -> Unit) {
        listeners.remove(listener)
    }

    fun publish(event: ExerciseEvent) {
        listeners.forEach { listener ->
            listener.invoke(event)
        }
    }
}