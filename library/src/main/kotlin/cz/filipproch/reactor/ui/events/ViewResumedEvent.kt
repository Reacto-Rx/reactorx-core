package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object ViewResumedEvent : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewResumed(): Observable<ViewResumedEvent> {
    return ofType(ViewResumedEvent::class.java)
}