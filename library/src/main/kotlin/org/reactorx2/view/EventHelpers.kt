package org.reactorx2.view

import io.reactivex.Observable
import io.reactivex.rxkotlin.cast
import org.reactorx2.ext.takeUntilViewStopped
import org.reactorx2.state.StateStore
import org.reactorx2.state.StateStoreTransformer
import org.reactorx2.state.epic
import org.reactorx2.state.model.Action
import org.reactorx2.view.events.ViewStarted

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