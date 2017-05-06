package cz.filipproch.reactor.ui

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

    private val viewDestroyedDisposable = CompositeDisposable()
    private val disposable = CompositeDisposable()

    private var translator: T? = null

    fun onViewCreated(translator: T) {
        reactorView.onEmittersInit()

        emittersInitialized = true

        disposeOnViewDestroyed(
                Observable.merge(eventEmitters)
                        .subscribe {
                            if (this.translator != null) {
                                eventSubject.onNext(it)
                            } else {
                                eventBuffer.add(it)
                            }
                        })


        this.translator = translator

        val uiModelStream = translator.bindView(eventSubject)
                .publish()
        reactorView.onConnectModelChannel(uiModelStream)
        disposable.add(uiModelStream.connect())

        val uiActionStream = translator.observeActions()
                .publish()
        reactorView.onConnectActionChannel(uiActionStream)
        disposable.add(uiActionStream.connect())

        if (eventBuffer.isNotEmpty()) {
            eventBuffer.forEach { eventSubject.onNext(it) }
            eventBuffer.clear()
        }
    }

    fun onViewDestroyed() {
        viewDestroyedDisposable.dispose()
        viewDestroyedDisposable.clear()

        emittersInitialized = false
        eventEmitters.clear()
    }

    fun destroy() {
        translator?.unbindView()
        disposable.dispose()
    }

    fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        if (emittersInitialized) {
            throw RuntimeException()
        }
        eventEmitters.add(emitter)
    }

    fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>) {
        disposable.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(receiverAction)
        )
    }

    private fun disposeOnViewDestroyed(disposable: Disposable) {
        this.viewDestroyedDisposable.add(disposable)
    }

}