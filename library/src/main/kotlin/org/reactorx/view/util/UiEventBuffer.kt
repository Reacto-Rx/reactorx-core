package org.reactorx.view.util

import org.reactorx.view.model.UiEvent

/**
 * TODO
 */
internal interface UiEventBuffer {

    val bufferedEvents: MutableList<UiEvent>

    val isPresenterConnected: Boolean

    fun dispatchAllBufferedEvents() {
        if (bufferedEvents.isNotEmpty()) {
            while (bufferedEvents.isNotEmpty()) {
                dispatch(bufferedEvents.removeAt(0))
            }
        }
    }

    fun bufferEvent(event: UiEvent) {
        synchronized(bufferedEvents) {
            bufferedEvents.add(event)
        }
    }

    fun dispatch(event: UiEvent)

}