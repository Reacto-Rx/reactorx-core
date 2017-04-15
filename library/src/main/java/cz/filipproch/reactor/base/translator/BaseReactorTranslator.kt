package cz.filipproch.reactor.base.translator

import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.rx.TypeBehaviorSubject
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.ReplaySubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class BaseReactorTranslator : ReactorTranslator {

    private val inputSubject = ReplaySubject.create<ReactorUiEvent>()
    private val outputSubject = TypeBehaviorSubject.create<ReactorUiModel>()

    private val instanceDisposable = CompositeDisposable()

    override fun bindView(events: Observable<out ReactorUiEvent>): Observable<out ReactorUiModel> {
        events.subscribe(inputSubject)
        return outputSubject.asObservable()
    }

    override fun unbindView() {

    }

    override fun onDestroyed() {
        instanceDisposable.dispose()
    }

    fun translate(reaction: (events: Observable<out ReactorUiEvent>) -> Observable<out ReactorUiModel>) {
        this.translate(object : EventReaction {
            override fun translate(events: Observable<out ReactorUiEvent>): Observable<out ReactorUiModel> {
                return reaction.invoke(events)
            }
        })
    }

    fun translate(reaction: EventReaction) {
        reaction.translate(inputSubject).subscribe(outputSubject)
    }

}