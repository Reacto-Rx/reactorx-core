package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * [ReactorUiEvent] that's dispatched by some [cz.filipproch.reactor.base.view.ReactorView] implementations
 * when the [cz.filipproch.reactor.base.view.ReactorView] is destroyed
 *
 * <b>Important note</b>: it's not guaranteed for this event to be dispatched, if the [cz.filipproch.reactor.base.translator.ReactorTranslator]
 * is being destroyed with the given [cz.filipproch.reactor.base.view.ReactorView] (for example, [android.app.Activity] is finishing)
 *
 * @author Filip Prochazka (@filipproch)
 */
object ViewDestroyedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewDestroyed(): Observable<ViewDestroyedEvent> {
    return ofType(ViewDestroyedEvent::class.java)
}