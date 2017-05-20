package cz.filipproch.reactor.rx

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.ConcurrentHashMap

/**
 * Helper class, implementation on top of [BehaviorSubject],
 * requires all emitted items to implement [TypedObject] to obtain [Class].
 *
 * Emits last received instance of each unique [Class] type upon subscription
 * and than continues emitting received objects.
 */
internal class TypeBehaviorSubject<T : TypedObject> private constructor() : Subject<T>() {

    internal val typesMemory = ConcurrentHashMap<Class<*>, T>()

    internal val subject = PublishSubject.create<T>()

    override fun hasComplete(): Boolean {
        return subject.hasComplete()
    }

    override fun hasObservers(): Boolean {
        return subject.hasObservers()
    }

    override fun hasThrowable(): Boolean {
        return subject.hasThrowable()
    }

    override fun subscribeActual(observer: Observer<in T>?) {
        subject.subscribeActual(observer)

        typesMemory.forEach { (_, value) ->
            observer?.onNext(value)
        }
    }

    override fun onComplete() {
        subject.onComplete()
    }

    override fun onSubscribe(d: Disposable?) {
        subject.onSubscribe(d)
    }

    override fun getThrowable(): Throwable {
        return subject.throwable
    }

    override fun onError(e: Throwable?) {
        subject.onError(e)
    }

    override fun onNext(t: T) {
        cacheToTypeMemory(t)

        subject.onNext(t)
    }

    private fun cacheToTypeMemory(t: T) {
        typesMemory[t.getType()] = t
    }

    companion object {

        fun <T : TypedObject> create(): TypeBehaviorSubject<T> {
            return TypeBehaviorSubject()
        }

    }

}