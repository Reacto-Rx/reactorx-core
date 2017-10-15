package org.reactorx.ext

import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import org.reactorx.util.takeFirst
import org.reactorx.view.events.ViewStarted
import org.reactorx.view.events.ViewStopped

/**
 * Filters [Observable] stream to [ViewStarted]
 */
fun <T> Observable<T>.whenViewStarted(): Observable<ViewStarted> = ofType()

/**
 * Filters [Observable] stream to [ViewStarted], takes only first emission
 */
fun <T> Observable<T>.whenViewStartedFirstTime(): Observable<ViewStarted> {
    return whenViewStarted()
            .takeFirst()
}

/**
 * @author Filip Prochazka (@filipproch)
 */
fun <T> Observable<T>.takeUntilViewStopped(
        viewEvents: Observable<*>
): Observable<T> = takeUntil(viewEvents.ofType<ViewStopped>())