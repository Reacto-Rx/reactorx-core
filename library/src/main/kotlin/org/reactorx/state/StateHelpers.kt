package org.reactorx.state

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import org.reactorx.state.model.Action

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun plainTransformer(
        crossinline transformFun: (Observable<Action>) -> Observable<Action>
): ObservableTransformer<Action, Action> = transformer<Action> { _, allEvents ->
    transformFun.invoke(allEvents)
}

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun <reified T> transformer(
        crossinline transformFun: (inputStream: Observable<T>, stream: Observable<Action>) -> Observable<Action>
): ObservableTransformer<Action, Action> {
    return ObservableTransformer { transformFun.invoke(it.ofType(T::class.java), it) }
}

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun <reified T> flatMapTransformer(
        crossinline transformFun: (value: T, stream: Observable<Action>) -> Observable<out Action>
): ObservableTransformer<Action, Action> = transformer<T> { events, allEvents ->
    events.flatMap { value ->
        transformFun.invoke(value, allEvents)
    }
}

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun <reified T> switchMapTransformer(
        crossinline transformFun: (value: T, stream: Observable<Action>) -> Observable<out Action>
): ObservableTransformer<Action, Action> = transformer<T> { events, allEvents ->
    events.switchMap { value ->
        transformFun.invoke(value, allEvents)
    }
}