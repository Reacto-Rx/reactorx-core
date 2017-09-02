package org.reactorx.presenter

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.cast
import org.reactorx.state.StateStore
import org.reactorx.state.model.Action
import org.reactorx.view.model.UiEvent

/**
 * @author Filip Prochazka (@filipproch)
 */
abstract class Presenter<M> : ViewModel() {

    abstract val initialState: M

    private lateinit var stateStore: StateStore<M>

    var isDestroyed: Boolean = false

    open protected val transformers: Array<ObservableTransformer<Action, Action>> = emptyArray()
    open protected val middleware: Array<ObservableTransformer<Action, Action>> = emptyArray()

    fun dispatch(uiEvent: UiEvent) {
        if (isDestroyed) {
            throw IllegalStateException("Presenter is destroyed and unusable")
        }

        stateStore.dispatch(uiEvent)
    }

    fun destroySelf() {
        onCleared()
    }

    open fun onPostCreated() {
        stateStore = StateStore.Builder(initialState)
                .enableInputPasstrough { it !is UiEvent }
                .withReducer(this::reduceState)
                .withTransformer(*transformers)
                .withMiddleware(*middleware)
                .errorCallback(this::onError)
                // hack not to break build, TODO: remove in beta
                .extraTransformerObservablesObtainer {
                    onCreateStreams(it.filter {
                        it is UiEvent
                    }.cast())
                }
                .build()
    }

    override fun onCleared() {
        super.onCleared()
        stateStore.dispose()
        isDestroyed = true
    }

    private fun onError(throwable: Throwable) {
        onErrorInStream(throwable)
        onErrorInTransformers(throwable)
    }

    @Deprecated("Use onErrorInStream (both are now called at the same time",
            ReplaceWith("onErrorInStream(error)"))
    open fun onErrorInTransformers(error: Throwable) {
    }

    open fun onErrorInStream(error: Throwable) {
    }

    fun observeUiModel() = stateStore.observe()

    @Deprecated("Replaced by transformers & middleware arrays")
    open protected fun onCreateStreams(
            shared: Observable<out UiEvent>
    ): Array<Observable<out org.reactorx.presenter.model.Action>> {
        return emptyArray()
    }

    open protected fun reduceState(previousState: M, action: Action): M {
        return if (action is org.reactorx.presenter.model.Action) {
            this.stateReducer(previousState, action)
        } else {
            previousState
        }
    }

    @Deprecated("Replaced by reduceState", ReplaceWith("reduceState(previousState, action)"))
    open protected fun stateReducer(previousState: M, action: org.reactorx.presenter.model.Action): M {
        return previousState
    }

}