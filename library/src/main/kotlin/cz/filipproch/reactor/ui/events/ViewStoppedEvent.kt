package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * Represents [android.app.Activity.onStop] call in the Android Activity lifecycle
 *
 * Used by some built-in [cz.filipproch.reactor.base.view.ReactorView] implementations
 */
object ViewStoppedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewStopped(): Observable<ViewStoppedEvent> {
    return ofType(ViewStoppedEvent::class.java)
}