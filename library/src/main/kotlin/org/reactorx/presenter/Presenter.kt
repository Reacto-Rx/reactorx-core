package org.reactorx.presenter

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.reactorx.presenter.model.Action
import org.reactorx.view.model.UiEvent

/**
 * @author Filip Prochazka (@filipproch)
 */
abstract class Presenter<M> : ViewModel() {

    abstract val initialState: M

    open val uiEvents: Subject<UiEvent> = PublishSubject.create()

    private lateinit var transformer: ObservableTransformer<UiEvent, M>

    protected lateinit var uiModelObservable: Observable<M>

    fun onPostCreated() {
        transformer = ObservableTransformer { events ->
            events.publish { shared ->
                Observable.mergeArray(*onCreateStreams(shared))
                        .doOnError { onErrorInTransformers(it) }
            }.scan(initialState, {
                previousState, action ->
                stateReducer(previousState, action)
            })
        }

        uiModelObservable = createUiModelObservable()
    }

    open protected fun createUiModelObservable(): Observable<M> {
        return uiEvents.compose(transformer)
                .replay(1).autoConnect()
                .doOnError { onErrorInStream(it) }
    }

    open fun onErrorInTransformers(error: Throwable) {
    }

    open fun onErrorInStream(error: Throwable) {
    }

    open fun observeUiModel(): Observable<M> {
        return uiModelObservable
    }

    abstract protected fun onCreateStreams(shared: Observable<out UiEvent>): Array<Observable<out Action>>

    abstract protected fun stateReducer(previousState: M, action: Action): M

}