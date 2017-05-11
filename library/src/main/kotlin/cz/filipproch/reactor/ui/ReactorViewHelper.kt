package cz.filipproch.reactor.ui

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

/**
 * Helper class for [ReactorView] implementations
 *
 * @author Filip Prochazka (@filipproch)
 */
class ReactorViewHelper<T : ReactorTranslator>(val reactorView: ReactorView<T>) {

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

    var translator: T? = null

    fun onReadyToRegisterEmitters() {
        isEmittersRegistrationAllowed = true
        reactorView.onEmittersInit()
        isEmittersRegistrationAllowed = false
    }

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
        reactorView.onConnectModelChannel(uiModelStream)
        reactorView.onConnectModelStream(uiModelStream)
        viewBoundDisposable?.add(uiModelStream.connect())
    }

    private fun connectUiActions(translator: T) {
        val uiActionStream = translator.observeUiActions()
                .publish()
        reactorView.onConnectActionChannel(uiActionStream)
        reactorView.onConnectActionStream(uiActionStream)
        viewBoundDisposable?.add(uiActionStream.connect())
    }

    fun onViewNotUsable() {
        viewBoundDisposable?.dispose()
        viewBoundDisposable = null
    }

    fun onViewDestroyed() {
        instanceDisposable.dispose()

        translator?.unbindView()
        eventSubject = null
    }

    fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        if (isEmittersRegistrationAllowed.not()) {
            throw IllegalLifecycleOperation("registerEmitter can be only called in onEmittersInit()")
        }
        emitter.subscribe(emitterConsumer)
    }

    fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>) {
        viewBoundDisposable?.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(receiverAction)
        )
    }

    class IllegalLifecycleOperation(message: String) : RuntimeException(message)

}