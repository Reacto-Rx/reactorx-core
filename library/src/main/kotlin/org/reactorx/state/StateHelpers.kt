package org.reactorx.state

import io.reactivex.Observable
import org.reactorx.state.model.Action
import org.reactorx.state.util.castTo

/**
 * Transform function which transforms [Observable] stream of [Action]s to another stream of [Action]s.
 */
inline fun <reified T, reified S> epic(
        crossinline transformFun: (inputStream: Observable<T>, stream: Observable<Action>, store: StateStore<S>) -> Observable<Action>
): StateStoreTransformer<Action, Action> {
    return StateStoreTransformer { stream, store ->
        transformFun.invoke(stream.ofType(T::class.java), stream, store.castTo())
    }
}

/**
 * Transformer function to transform stream of [Action] to another stream of [Action]. Specific for
 * use with [StateStore], contains it's instance as param.
 */
inline fun <reified S> plainEpic(
        crossinline transformFun: (Observable<Action>, store: StateStore<S>) -> Observable<Action>
): StateStoreTransformer<Action, Action> = epic<Action, S> { _, allEvents, store ->
    transformFun.invoke(allEvents, store)
}

/**
 * TODO
 */
inline fun <reified T, reified S> flatMapEpic(
        crossinline transformFun: (value: T, stream: Observable<Action>, store: StateStore<S>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = epic<T, S> { events, allEvents, store ->
    events.flatMap { value ->
        transformFun.invoke(value, allEvents, store)
    }
}

/**
 * TODO
 */
inline fun <reified T> transformer(
        crossinline transformFun: (inputStream: Observable<T>, stream: Observable<Action>) -> Observable<Action>
): StateStoreTransformer<Action, Action> = epic<T, Any> { inputStream, stream, _ ->
    transformFun.invoke(inputStream, stream)
}

/**
 * Transformer function to transform stream of [Action] to another stream of [Action]
 */
inline fun plainTransformer(
        crossinline transformFun: (Observable<Action>) -> Observable<Action>
): StateStoreTransformer<Action, Action> = plainEpic<Any> { stream, _ ->
    transformFun.invoke(stream)
}

/**
 * TODO
 */
inline fun <reified T> flatMapTransformer(
        crossinline transformFun: (value: T, stream: Observable<Action>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = flatMapEpic<T, Any> { value, allEvents, _ ->
    transformFun.invoke(value, allEvents)
}

/**
 * TODO
 */
inline fun <reified T> switchMapTransformer(
        crossinline transformFun: (value: T, stream: Observable<Action>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = transformer<T> { events, allEvents ->
    events.switchMap { value ->
        transformFun.invoke(value, allEvents)
    }
}