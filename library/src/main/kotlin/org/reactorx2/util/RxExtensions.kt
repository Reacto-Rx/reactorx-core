package org.reactorx2.util

import io.reactivex.Observable

/**
 * Filters only first [count] emissions from the source stream
 */
fun <T> Observable<T>.takeFirst(
        count: Long = 1
): Observable<T> = take(count)