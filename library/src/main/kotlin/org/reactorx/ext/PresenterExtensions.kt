package org.reactorx.ext

import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import org.reactorx.view.events.ViewStarted
import org.reactorx.view.events.ViewStopped

/**
 * @author Filip Prochazka (@filipproch)
 */
fun <T> Observable<T>.whenViewStarted(): Observable<ViewStarted> = ofType()

/**
 * @author Filip Prochazka (@filipproch)
 */
fun <T> Observable<T>.whenViewStartedFirstTime(): Observable<ViewStarted> {
    return whenViewStarted()
            .take(1)
}

/**
 * @author Filip Prochazka (@filipproch)
 */
fun <T> Observable<T>.takeUntilViewStopped(
        viewEvents: Observable<*>
): Observable<T> = takeUntil(viewEvents.ofType<ViewStopped>())

/**
 * @author Filip Prochazka (@filipproch)
 */
fun <T> Observable<T>.takeFirst(
        count: Long = 1
): Observable<T> = take(count)