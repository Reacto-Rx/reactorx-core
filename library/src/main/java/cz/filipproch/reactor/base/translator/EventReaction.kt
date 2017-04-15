package cz.filipproch.reactor.base.translator

import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import io.reactivex.Observable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
interface EventReaction {
    fun translate(events: Observable<out ReactorUiEvent>): Observable<out ReactorUiModel>
}
