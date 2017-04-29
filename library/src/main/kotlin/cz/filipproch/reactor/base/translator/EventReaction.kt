package cz.filipproch.reactor.base.translator

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
interface EventReaction {
    fun react(events: Observable<out ReactorUiEvent>): Disposable
}