package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * Represents [android.app.Activity.onPause] call in the Android Activity lifecycle
 *
 * Used by some built-in [cz.filipproch.reactor.base.view.ReactorView] implementations
 */
object ViewPausedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewPaused(): Observable<ViewPausedEvent> {
    return ofType(ViewPausedEvent::class.java)
}