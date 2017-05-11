package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * [ReactorUiEvent] that's dispatched by some [cz.filipproch.reactor.base.view.ReactorView] implementations
 * when the [cz.filipproch.reactor.base.view.ReactorView] is paused
 *
 * @author Filip Prochazka (@filipproch)
 */
object ViewPausedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewPaused(): Observable<ViewPausedEvent> {
    return ofType(ViewPausedEvent::class.java)
}