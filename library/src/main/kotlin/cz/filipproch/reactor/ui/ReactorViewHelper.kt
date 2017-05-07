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

    private val eventEmitters = mutableListOf<Observable<out ReactorUiEvent>>()
    private var emittersInitialized: Boolean = false

    private val eventBuffer = mutableListOf<ReactorUiEvent>()
    private val eventSubject = PublishSubject.create<ReactorUiEvent>()

    private val viewBoundDisposable = CompositeDisposable()
    private val instanceDisposable = CompositeDisposable()

    private var translator: T? = null

    fun bindTranslatorWithView(translator: T) {
        reactorView.onEmittersInit()

        emittersInitialized = true

        viewBoundDisposable.add(
                Observable.merge(eventEmitters)
                        .subscribe {
                            if (this.translator != null) {
                                eventSubject.onNext(it)
                            } else {
                                eventBuffer.add(it)
                            }
                        })
        eventEmitters.clear()


        this.translator = translator

        val uiModelStream = translator.bindView(eventSubject)
                .publish()
        reactorView.onConnectModelChannel(uiModelStream)
        reactorView.onConnectModelStream(uiModelStream)
        viewBoundDisposable.add(uiModelStream.connect())

        val uiActionStream = translator.observeActions()
                .publish()
        reactorView.onConnectActionChannel(uiActionStream)
        reactorView.onConnectActionStream(uiActionStream)
        viewBoundDisposable.add(uiActionStream.connect())

        if (eventBuffer.isNotEmpty()) {
            eventBuffer.forEach { eventSubject.onNext(it) }
            eventBuffer.clear()
        }
    }

    fun unbindObserverFromView() {
        viewBoundDisposable.dispose()
        viewBoundDisposable.clear()

        emittersInitialized = false
        eventEmitters.clear()

        translator?.unbindView()
    }

    fun destroy() {
        instanceDisposable.dispose()
    }

    fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        if (emittersInitialized) {
            throw RuntimeException()
        }
        eventEmitters.add(emitter)
    }

    fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>) {
        viewBoundDisposable.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(receiverAction)
        )
    }

}