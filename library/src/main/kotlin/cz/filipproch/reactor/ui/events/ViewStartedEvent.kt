package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * Represents [android.app.Activity.onStart] call in the Android Activity lifecycle
 *
 * Used by some built-in [cz.filipproch.reactor.base.view.ReactorView] implementations
 */
object ViewStartedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewStarted(): Observable<ViewStartedEvent> {
    return ofType(ViewStartedEvent::class.java)
}