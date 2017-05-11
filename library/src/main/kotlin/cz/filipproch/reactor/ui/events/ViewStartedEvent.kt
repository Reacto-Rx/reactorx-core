package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * [ReactorUiEvent] that's dispatched by some [cz.filipproch.reactor.base.view.ReactorView] implementations
 * when the [cz.filipproch.reactor.base.view.ReactorView] is started
 *
 * @author Filip Prochazka (@filipproch)
 */
object ViewStartedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewStarted(): Observable<ViewStartedEvent> {
    return ofType(ViewStartedEvent::class.java)
}