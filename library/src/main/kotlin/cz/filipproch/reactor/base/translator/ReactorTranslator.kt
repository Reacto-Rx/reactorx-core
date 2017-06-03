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
 * The main [IReactorTranslator] implementation. It uses [io.reactivex.subjects.Subject]
 * internally to keep the streams of data intact (without getting them disposed)
 */
abstract class ReactorTranslator : IReactorTranslator {

    private val inputSubject = PublishSubject.create<ReactorUiEvent>()
    private val outputModelSubject = TypeBehaviorSubject.create()
    private val outputActionSubject = PublishSubject.create<ReactorUiAction>()

    private val instanceDisposable = CompositeDisposable()
    private var viewDisposable: Disposable? = null

    /**
     * Stream of events from ReactorView
     */
    val eventStream: Observable<ReactorUiEvent>
        get() = inputSubject

    /**
     * Whether the instance of [ReactorTranslator] was initialized ([onInstanceCreated] was called)
     */
    var isCreated: Boolean = false
        private set

    /**
     * Whether the instance of [ReactorTranslator] was destroyed ([onBeforeInstanceDestroyed] was called)
     */
    var isDestroyed: Boolean = false
        private set

    override fun bindView(events: Observable<out ReactorUiEvent>) {
        viewDisposable = events.subscribe {
            inputSubject.onNext(it)
        }
    }

    override fun observeUiModels(): Observable<out ReactorUiModel> {
        return outputModelSubject
    }

    override fun observeUiActions(): Observable<out ReactorUiAction> {
        return outputActionSubject
    }

    override fun unbindView() {
        viewDisposable?.dispose()
        viewDisposable = null
    }

    override final fun onInstanceCreated() {
        onCreated()
        isCreated = true
    }

    override final fun onBeforeInstanceDestroyed() {
        onBeforeDestroyed()

        instanceDisposable.dispose()
        isDestroyed = true
    }

    /**
     * Called when new instance of this translator was created
     *
     * Method for inheriting translators to be used instead of [onInstanceCreated]
     */
    abstract fun onCreated()

    /**
     * Called before the instance is destroyed and thrown away
     *
     * Method for inheriting translators to be used instead of [onBeforeInstanceDestroyed]
     */
    open fun onBeforeDestroyed() {
    }

    fun translateToModel(reaction: Observable<out ReactorUiEvent>.() -> Observable<out ReactorUiModel>) {
        instanceDisposable.add(reaction.invoke(inputSubject)
                .subscribe(outputModelSubject::onNext))
    }

    @Deprecated("Use the lambda syntax")
    fun translate(translation: EventModelTranslation) {
        instanceDisposable.add(
                translation.translate(inputSubject)
                        .subscribe(outputModelSubject::onNext))
    }

    fun translateToAction(reaction: Observable<out ReactorUiEvent>.() -> Observable<out ReactorUiAction>) {
        instanceDisposable.add(reaction.invoke(inputSubject)
                .subscribe(outputActionSubject::onNext))
    }

    @Deprecated("Use the lambda syntax")
    fun translate(translation: EventActionTranslation) {
        instanceDisposable.add(
                translation.translate(inputSubject)
                        .subscribe(outputActionSubject::onNext))
    }

    fun reactTo(reaction: Observable<out ReactorUiEvent>.() -> Disposable) {
        instanceDisposable.add(reaction.invoke(inputSubject))
    }

    @Deprecated("Use the lambda syntax")
    fun reactTo(reaction: EventReaction) {
        instanceDisposable.add(reaction.react(inputSubject))
    }

    /**
     * Interface used internally by [ReactorTranslator] to register [ReactorUiAction] translations
     */
    interface EventActionTranslation {
        fun translate(events: Observable<out ReactorUiEvent>): Observable<out ReactorUiAction>
    }

    /**
     * Interface used internally by [ReactorTranslator] to register [ReactorUiModel] translations
     */
    interface EventModelTranslation {
        fun translate(events: Observable<out ReactorUiEvent>): Observable<out ReactorUiModel>
    }

    /**
     *  Interface used internally by [ReactorTranslator] to register reactions
     */
    @Deprecated("Use the lambda syntax")
    interface EventReaction {
        fun react(events: Observable<out ReactorUiEvent>): Disposable
    }

}