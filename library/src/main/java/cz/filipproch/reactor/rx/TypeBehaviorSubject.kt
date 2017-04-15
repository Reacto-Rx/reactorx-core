package cz.filipproch.reactor.rx

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class TypeBehaviorSubject<T: TypedObject> private constructor() : Observer<T> {

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

    fun asObservable(): Observable<T> {
        return subject.concatMapIterable {
            it.entries.map { it.value }
        }
    }

    companion object {

        fun <T: TypedObject> create(): TypeBehaviorSubject<T> {
            return TypeBehaviorSubject()
        }

    }

}