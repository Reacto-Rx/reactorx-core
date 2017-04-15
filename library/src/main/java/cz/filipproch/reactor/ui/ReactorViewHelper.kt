package cz.filipproch.reactor.ui

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.base.view.ReactorView
import cz.filipproch.reactor.ui.model.ReceiverAction
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.ReplaySubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class ReactorViewHelper<T : ReactorTranslator>(val reactorView: ReactorView<T>) {

    private val eventEmitters = mutableListOf<Observable<out ReactorUiEvent>>()
    private var emittersInitialized: Boolean = false

    private val eventSubject = ReplaySubject.create<ReactorUiEvent>()

    private val disposable = CompositeDisposable()

    private var translator: T? = null

    fun onViewCreated() {
        reactorView.onEmittersInit()

        emittersInitialized = true

        disposeOnViewDestroyed(
                Observable.merge(eventEmitters)
                        .subscribe { eventSubject.onNext(it) })
    }

    fun onTranslatorAttached(translator: T) {
        this.translator = translator

        val uiModelStream = translator.bindView(eventSubject).share()
        reactorView.onConnectModelChannel(uiModelStream)
    }

    fun onTranslatorDetached() {
        translator?.unbindView()
    }

    fun onViewDestroyed() {
        disposable.dispose()
    }

    fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        if (emittersInitialized) {
            throw RuntimeException()
        }
        eventEmitters.add(emitter)
    }

    fun <T : ReactorUiModel> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: ReceiverAction<T>) {
        disposable.add(
                observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            receiverAction.invoke(it)
                        }
        )
    }

    private fun disposeOnViewDestroyed(disposable: Disposable) {
        this.disposable.add(disposable)
    }

}