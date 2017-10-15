package org.reactorx.view.util

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 * TODO
 */
interface ObservableViewUtils {

    fun disposeWithView(disposable: Disposable)

    fun disposeWithView(body: () -> Disposable)

    fun <T> Observable<T>.subscribeWithView(action: (T) -> Unit) {
        disposeWithView {
            this.subscribe(action)
        }
    }

    fun <T> Observable<T>.subscribeWithView(consumer: Consumer<in T>) {
        disposeWithView {
            this.subscribe(consumer)
        }
    }

}