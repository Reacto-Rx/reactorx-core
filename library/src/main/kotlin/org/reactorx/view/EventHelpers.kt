package org.reactorx.view

import io.reactivex.Observable
import io.reactivex.rxkotlin.cast
import org.reactorx.ext.takeUntilViewStopped
import org.reactorx.state.StateStore
import org.reactorx.state.StateStoreTransformer
import org.reactorx.state.epic
import org.reactorx.state.model.Action
import org.reactorx.view.events.ViewStarted

/**
 * TODO
 */
inline fun <reified S> viewStartedEpic(
        terminateViewStopped: Boolean = true,
        endWithValue: Action? = null,
        crossinline transformFun: (stream: Observable<Action>, store: StateStore<S>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = epic<ViewStarted, S> { events, allEvents, store ->
    events.switchMap {
        var observable: Observable<Action> = transformFun.invoke(allEvents, store).cast()

        observable = if (terminateViewStopped) {
            observable.takeUntilViewStopped(allEvents)
        } else {
            observable
        }

        if (endWithValue != null) {
            observable = observable.concatWith(Observable.just(endWithValue))
        }

        observable
    }
}

/**
 * TODO
 */
inline fun viewStartedTransformer(
        terminateViewStopped: Boolean = true,
        endWithValue: Action? = null,
        crossinline transformFun: (allEvents: Observable<Action>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = viewStartedEpic<Any>(
        terminateViewStopped, endWithValue
) { stream, _ ->
    transformFun.invoke(stream)
}