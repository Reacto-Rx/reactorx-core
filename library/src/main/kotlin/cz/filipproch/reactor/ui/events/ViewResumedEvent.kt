package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * Represents [android.app.Activity.onResume] call in the Android Activity lifecycle
 *
 * Used by some built-in [cz.filipproch.reactor.base.view.ReactorView] implementations
 */
object ViewResumedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewResumed(): Observable<ViewResumedEvent> {
    return ofType(ViewResumedEvent::class.java)
}