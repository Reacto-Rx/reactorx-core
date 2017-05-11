package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * [ReactorUiEvent] that's dispatched by some [cz.filipproch.reactor.base.view.ReactorView] implementations
 * when the [cz.filipproch.reactor.base.view.ReactorView] is stopped
 *
 * @author Filip Prochazka (@filipproch)
 */
object ViewStoppedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewStopped(): Observable<ViewStoppedEvent> {
    return ofType(ViewStoppedEvent::class.java)
}