package cz.filipproch.reactor.rx

import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.base.view.StateAwareUiModel
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
internal class TypeBehaviorSubject private constructor() : Subject<ReactorUiModel>() {

    internal val typesMemory = ConcurrentHashMap<Class<*>, ReactorUiModel>()

    internal val subject = PublishSubject.create<ReactorUiModel>()

    override fun hasComplete(): Boolean {
        return subject.hasComplete()
    }

    override fun hasObservers(): Boolean {
        return subject.hasObservers()
    }

    override fun hasThrowable(): Boolean {
        return subject.hasThrowable()
    }

    override fun subscribeActual(observer: Observer<in ReactorUiModel>?) {
        subject.subscribeActual(observer)

        typesMemory.forEach { (_, value) ->
            if (value is StateAwareUiModel) {
                value.setCached()
            }
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

    override fun onNext(t: ReactorUiModel) {
        cacheToTypeMemory(t)

        subject.onNext(t)
    }

    private fun cacheToTypeMemory(t: ReactorUiModel) {
        typesMemory[t.getType()] = t
    }

    companion object {

        fun create(): TypeBehaviorSubject {
            return TypeBehaviorSubject()
        }

    }

}