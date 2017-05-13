package cz.filipproch.reactor.rx

import android.support.annotation.NonNull
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Helper class, implementation on top of [BehaviorSubject],
 * requires all emitted items to implement [TypedObject] to obtain [Class].
 *
 * Emits last received instance of each unique [Class] type upon subscription
 * and than continues emitting received objects.
 */
class TypeBehaviorSubject<T : TypedObject> private constructor() : Observer<T> {

    private val subject = BehaviorSubject.create<Map<Class<*>, T>>()

    private val typesMap = mutableMapOf<Class<*>, T>()

    init {
        subject.onNext(mutableMapOf())
    }

    override fun onComplete() {
        subject.onComplete()
    }

    override fun onSubscribe(d: Disposable?) {
        subject.onSubscribe(d)
    }

    override fun onNext(value: T) {
        typesMap[value.getType()] = value
        return subject.onNext(typesMap)
    }

    override fun onError(e: Throwable?) {
        return subject.onError(e)
    }

    /**
     * Returns [Observable] that emits the unique instances of
     * given [TypedObject]s
     */
    @NonNull
    fun asObservable(): Observable<T> {
        // todo: fix, do not emit all values every time !!!
        return subject.concatMapIterable {
            it.entries.map { it.value }
        }
    }

    companion object {

        fun <T : TypedObject> create(): TypeBehaviorSubject<T> {
            return TypeBehaviorSubject()
        }

    }

}