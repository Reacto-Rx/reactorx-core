package org.reactorx.state

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import org.reactorx.state.model.Action

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun plainTransformer(
        crossinline transformFun: (Observable<Action>) -> Observable<Action>
) = transformer(transformFun)

/**
 * @author Filip Prochazka (@filipproch)
 */
inline fun <reified T> transformer(
        crossinline transformFun: (Observable<T>) -> Observable<Action>
): ObservableTransformer<Action, Action> {
    return ObservableTransformer { transformFun.invoke(it.ofType(T::class.java)) }
}