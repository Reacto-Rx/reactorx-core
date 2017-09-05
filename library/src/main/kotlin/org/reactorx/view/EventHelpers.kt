package org.reactorx.view

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import org.reactorx.ext.takeUntilViewStopped
import org.reactorx.state.model.Action
import org.reactorx.state.transformer
import org.reactorx.view.events.ViewStarted

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun viewStartedTransformer(
        terminateViewStopped: Boolean = true,
        crossinline transformFun: (allEvents: Observable<Action>) -> Observable<Action>
): ObservableTransformer<Action, Action> = transformer<ViewStarted> { events, allEvents ->
    events.flatMap {
        val observable = transformFun.invoke(allEvents)

        if (terminateViewStopped) {
            observable.takeUntilViewStopped(allEvents)
        } else {
            observable
        }
    }
}