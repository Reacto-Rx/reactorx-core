package org.reactorx2.ext

import io.reactivex.Observable
import io.reactivex.rxkotlin.ofType
import org.reactorx2.util.takeFirst
import org.reactorx2.view.events.ViewStarted
import org.reactorx2.view.events.ViewStopped

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