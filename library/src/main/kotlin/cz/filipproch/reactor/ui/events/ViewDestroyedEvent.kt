package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * Represents [android.app.Activity.onDestroy] call in the Android Activity lifecycle
 *
 * Used by some built-in [cz.filipproch.reactor.base.view.ReactorView] implementations
 *
 * <b>Important note</b>: it's not guaranteed for this event to be dispatched,
 * if the [cz.filipproch.reactor.base.translator.IReactorTranslator] is being
 * destroyed with the given [cz.filipproch.reactor.base.view.ReactorView]
 * (for example, [android.app.Activity] is finishing)
 */
object ViewDestroyedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewDestroyed(): Observable<ViewDestroyedEvent> {
    return ofType(ViewDestroyedEvent::class.java)
}