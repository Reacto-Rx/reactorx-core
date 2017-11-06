package org.reactorx2.state

import io.reactivex.Observable
import org.reactorx2.state.model.Action
import org.reactorx2.state.util.castTo

/**
 * Transforming function. Transforms stream of [Action]s to another stream of [Action]s. The input
 * stream is filtered based on provided type [T]. Is [StateStore] aware, and provides it's instance as a param.
 */
inline fun <reified T, reified S> epic(
        crossinline transformFun: (inputStream: Observable<T>, stream: Observable<Action>, store: StateStore<S>) -> Observable<Action>
): StateStoreTransformer<Action, Action> {
    return StateStoreTransformer { stream, store ->
        transformFun.invoke(stream.ofType(T::class.java), stream, store.castTo())
    }
}

/**
 * Transforming function. Transforms stream of [Action]s to another stream of [Action]s.
 * Is [StateStore] aware, and provides it's instance as a param.
 */
inline fun <reified S> plainEpic(
        crossinline transformFun: (Observable<Action>, store: StateStore<S>) -> Observable<Action>
): StateStoreTransformer<Action, Action> = epic<Action, S> { _, allEvents, store ->
    transformFun.invoke(allEvents, store)
}

/**
 * Transforming function. Essentially same as [epic] but the function is called inside the body
 * of [Observable.flatMap] operator, providing emitted value. Is [StateStore] aware, and provides
 * it's instance as a param.
 */
inline fun <reified T, reified S> flatMapEpic(
        crossinline transformFun: (value: T, stream: Observable<Action>, store: StateStore<S>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = epic<T, S> { events, allEvents, store ->
    events.flatMap { value ->
        transformFun.invoke(value, allEvents, store)
    }
}

/**
 * Transforming function. Transforms stream of [Action]s to another stream of [Action]s. The input
 * stream is filtered based on provided type [T].
 */
inline fun <reified T> transformer(
        crossinline transformFun: (inputStream: Observable<T>, stream: Observable<Action>) -> Observable<Action>
): StateStoreTransformer<Action, Action> = epic<T, Any> { inputStream, stream, _ ->
    transformFun.invoke(inputStream, stream)
}

/**
 * Transforming function. Transforms stream of [Action]s to another stream of [Action]s.
 */
inline fun plainTransformer(
        crossinline transformFun: (Observable<Action>) -> Observable<Action>
): StateStoreTransformer<Action, Action> = plainEpic<Any> { stream, _ ->
    transformFun.invoke(stream)
}

/**
 * Transforming function. Essentially same as [transformer] but the function is called inside the body
 * of [Observable.flatMap] operator, providing emitted value.
 */
inline fun <reified T> flatMapTransformer(
        crossinline transformFun: (value: T, stream: Observable<Action>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = flatMapEpic<T, Any> { value, allEvents, _ ->
    transformFun.invoke(value, allEvents)
}

/**
 * Transforming function. Essentially same as [transformer] but the function is called inside the body
 * of [Observable.switchMap] operator, providing emitted value.
 */
inline fun <reified T> switchMapTransformer(
        crossinline transformFun: (value: T, stream: Observable<Action>) -> Observable<out Action>
): StateStoreTransformer<Action, Action> = transformer<T> { events, allEvents ->
    events.switchMap { value ->
        transformFun.invoke(value, allEvents)
    }
}