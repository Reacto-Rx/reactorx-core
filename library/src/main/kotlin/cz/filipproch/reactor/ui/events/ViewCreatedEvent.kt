package cz.filipproch.reactor.ui.events

import android.os.Bundle
import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class ViewCreatedEvent(val savedInstanceState: Bundle?) : ReactorUiEvent

fun Observable<out ReactorUiEvent>.whenViewCreated(): Observable<ViewCreatedEvent> {
    return ofType(ViewCreatedEvent::class.java)
}