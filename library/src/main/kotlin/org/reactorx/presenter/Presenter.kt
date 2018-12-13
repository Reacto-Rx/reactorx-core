package org.reactorx.presenter

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.cast
import org.reactorx.state.StateStore
import org.reactorx.state.StateStoreTransformer
import org.reactorx.state.model.Action
import org.reactorx.view.model.UiEvent

/**
 * Presenter is a controller for a view. It separates the view specific logic from
 * the view.
 *
 * Holds an state for the view in a [StateStore]. Enforces unidirectional data flow,
 * as the only way to modify the state. [UiEvent] are dispatched using [dispatch], then
 * transformed with [transformers] to [Action]s which modify the internal state. The view
 * can then observe changes to the state using [observeUiModel]
 */
abstract class Presenter<M> : ViewModel() {

    private val stateStore: StateStore<M> by lazy {
        StateStore.Builder(initialState)
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

    /**
     * Returns latest state value
     */
    protected val currentState: M get() = stateStore.currentState

    /**
     * Initial internal state value
     */
    abstract val initialState: M

    /**
     * Returns true if this instance was already destroyed and internal resources were disposed
     */
    var isDestroyed: Boolean = false

    /**
     * Transformers that convert [UiEvent]s to [Action] that will be dispatched to modify the state
     */
    open protected val transformers: Array<StateStoreTransformer<Action, Action>> = emptyArray()

    /**
     * Transformers, that receive all dispatched [Action]s, after the state was modified
     * and returns stream of [Action]s
     */
    open protected val middleware: Array<StateStoreTransformer<Action, Action>> = emptyArray()

    /**
     * Invoked after the instance was created, internally instantiates
     * the [StateStore]
     */
    open fun onPostCreated() {
    }

    /**
     * Dispatch [uiEvent] to the underlying [StateStore]
     *
     * @throws IllegalStateException If [isDestroyed] returns true
     */
    fun dispatch(uiEvent: UiEvent) {
        if (isDestroyed) {
            throw IllegalStateException("Presenter is destroyed and unusable")
        }

        stateStore.dispatch(uiEvent)
    }

    /**
     * Destroys the instance and disposes resources
     */
    fun destroySelf() {
        onCleared()
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

    /**
     * Invoked when error is thrown by a transformer/middleware
     */
    open fun onErrorInStream(error: Throwable) {
    }

    @Deprecated("Replaced by observeStateChanges", ReplaceWith("observeStateChanges()"))
    fun observeUiModel() = observeStateChanges()

    /**
     * Stream of state values. Emissions happen whenever an [Action] is dispatched.
     */
    fun observeStateChanges() = stateStore.observe()

    @Deprecated("Replaced by transformers & middleware arrays")
    open protected fun onCreateStreams(
            shared: Observable<out UiEvent>
    ): Array<Observable<out org.reactorx.presenter.model.Action>> {
        return emptyArray()
    }

    /**
     * Function that takes [Action] and current state and returns a new state. Has to be
     * a pure function.
     */
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