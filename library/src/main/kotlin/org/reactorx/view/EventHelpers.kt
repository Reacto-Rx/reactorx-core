package org.reactorx.view

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.rxkotlin.cast
import org.reactorx.ext.takeUntilViewStopped
import org.reactorx.state.model.Action
import org.reactorx.state.transformer
import org.reactorx.view.events.ViewStarted

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun viewStartedTransformer(
        terminateViewStopped: Boolean = true,
        endWithValue: Action? = null,
        crossinline transformFun: (allEvents: Observable<Action>) -> Observable<out Action>
): ObservableTransformer<Action, Action> = transformer<ViewStarted> { events, allEvents ->
    events.switchMap {
        var observable: Observable<Action> = transformFun.invoke(allEvents).cast()

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