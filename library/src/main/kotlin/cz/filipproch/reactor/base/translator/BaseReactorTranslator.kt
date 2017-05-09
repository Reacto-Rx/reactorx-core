package cz.filipproch.reactor.base.translator

import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.rx.TypeBehaviorSubject
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class BaseReactorTranslator : ReactorTranslator {

    private val inputSubject = PublishSubject.create<ReactorUiEvent>()
    private val outputModelSubject = TypeBehaviorSubject.create<ReactorUiModel>()
    private val outputActionSubject = PublishSubject.create<ReactorUiAction>()

    private val instanceDisposable = CompositeDisposable()

    override fun bindView(events: Observable<out ReactorUiEvent>) {
        events.subscribe(inputSubject)
    }

    override fun observeUiModels(): Observable<out ReactorUiModel> {
        return outputModelSubject.asObservable()
    }

    override fun observeUiActions(): Observable<out ReactorUiAction> {
        return outputActionSubject
    }

    override fun unbindView() {
    }

    override fun onDestroyed() {
        instanceDisposable.dispose()
    }

    fun translateToModel(reaction: (events: Observable<out ReactorUiEvent>) -> Observable<out ReactorUiModel>) {
        this.translate(object : EventModelTranslation {
            override fun translate(events: Observable<out ReactorUiEvent>): Observable<out ReactorUiModel> {
                return reaction.invoke(events)
            }
        })
    }

    fun translate(translation: EventModelTranslation) {
        instanceDisposable.add(
                translation.translate(inputSubject)
                        .subscribe(outputModelSubject::onNext))
    }

    fun translateToAction(reaction: (events: Observable<out ReactorUiEvent>) -> Observable<out ReactorUiAction>) {
        this.translate(object : EventActionTranslation {
            override fun translate(events: Observable<out ReactorUiEvent>): Observable<out ReactorUiAction> {
                return reaction.invoke(events)
            }
        })
    }

    fun translate(translation: EventActionTranslation) {
        instanceDisposable.add(
                translation.translate(inputSubject)
                        .subscribe(outputActionSubject::onNext))
    }

    fun reactTo(reaction: (events: Observable<out ReactorUiEvent>) -> Disposable) {
        this.reactTo(object : EventReaction {
            override fun react(events: Observable<out ReactorUiEvent>): Disposable {
                return reaction.invoke(events)
            }
        })
    }

    fun reactTo(reaction: EventReaction) {
        instanceDisposable.add(reaction.react(inputSubject))
    }

}