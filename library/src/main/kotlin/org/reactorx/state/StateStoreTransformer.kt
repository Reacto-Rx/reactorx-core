package org.reactorx.state

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 * [ObservableTransformer] implementation that keeps reference to [StateStore]
 * and provides it as a param in the transformation call.
 */
open class StateStoreTransformer<U, D>(
        private var stateStore: StateStore<*>? = null,
        private val epic: (stream: Observable<U>, store: StateStore<*>) -> ObservableSource<D>
) : ObservableTransformer<U, D> {

    fun bindStateStore(stateStore: StateStore<*>) {
        this.stateStore = stateStore
    }

    final override fun apply(upstream: Observable<U>): ObservableSource<D> {
        return epic(upstream, checkNotNull(stateStore, { "StateStore not bound" }))
    }

    companion object {
        fun <U, D> from(transformer: ObservableTransformer<U, D>): StateStoreTransformer<U, D> {
            return StateStoreTransformer { stream, _ -> transformer.apply(stream) }
        }
    }

}