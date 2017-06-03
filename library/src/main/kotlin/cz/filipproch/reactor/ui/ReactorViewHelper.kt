package cz.filipproch.reactor.ui

import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

/**
 * Helper class for [ReactorView] implementations to manage
 * the connection between [ReactorView] and [IReactorTranslator]
 */
class ReactorViewHelper<T : IReactorTranslator>(val reactorView: ReactorView<T>) {

    private var isEmittersRegistrationAllowed = false

    private val eventBuffer = mutableListOf<ReactorUiEvent>()
    private var eventSubject: PublishSubject<ReactorUiEvent>? = PublishSubject.create()

    private var viewBoundDisposable: CompositeDisposable? = null
    private val instanceDisposable = CompositeDisposable()

    private val emitterConsumer = Consumer<ReactorUiEvent> {
        if (this.translator != null) {
            eventSubject?.onNext(it)
        } else {
            eventBuffer.add(it)
        }
    }

    /**
     * TODO
     */
    var translator: T? = null

    /**
     * TODO
     */
    fun onReadyToRegisterEmitters() {
        isEmittersRegistrationAllowed = true
        reactorView.onEmittersInit()
        isEmittersRegistrationAllowed = false
    }

    /**
     * TODO
     */
    fun bindTranslatorWithView(translator: T) {
        viewBoundDisposable = CompositeDisposable()
        this.translator = translator

        translator.bindView(checkNotNull(eventSubject))

        connectUiModels(translator)
        connectUiActions(translator)

        if (eventBuffer.isNotEmpty()) {
            eventBuffer.forEach { eventSubject?.onNext(it) }
            eventBuffer.clear()
        }
    }

    private fun connectUiModels(translator: T) {
        val uiModelStream = translator.observeUiModels()
                .publish()
        reactorView.onConnectModelStream(uiModelStream)

        viewBoundDisposable?.add(uiModelStream.connect())
    }

    private fun connectUiActions(translator: T) {
        val uiActionStream = translator.observeUiActions()
                .publish()
        reactorView.onConnectActionStream(uiActionStream)
        viewBoundDisposable?.add(uiActionStream.connect())
    }

    /**
     * TODO
     */
    fun onViewNotUsable() {
        translator?.unbindView()
        translator = null

        viewBoundDisposable?.dispose()
        viewBoundDisposable = null
    }

    /**
     * TODO
     */
    fun onViewDestroyed() {
        instanceDisposable.dispose()

        eventSubject = null
    }

    /**
     * TODO
     */
    fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        if (isEmittersRegistrationAllowed.not()) {
            throw IllegalLifecycleOperation("registerEmitter can be only called in onEmittersInit()")
        }
        emitter.subscribe(emitterConsumer)
    }

    /**
     * TODO
     */
    fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>) {
        viewBoundDisposable?.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(receiverAction)
        )
    }

    class IllegalLifecycleOperation(message: String) : RuntimeException(message)

}