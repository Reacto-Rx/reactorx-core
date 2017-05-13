package cz.filipproch.reactor.ui.events

import android.os.Bundle
import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * Represents [android.app.Activity.onCreate] call in the Android Activity lifecycle
 *
 * Used by some built-in [cz.filipproch.reactor.base.view.ReactorView] implementations
 */
class ViewCreatedEvent(val savedInstanceState: Bundle?) : ReactorUiEvent

/**
 * Helper filter for [ViewCreatedEvent]s
 */
fun Observable<out ReactorUiEvent>.whenViewCreated(): Observable<ViewCreatedEvent> {
    return ofType(ViewCreatedEvent::class.java)
}