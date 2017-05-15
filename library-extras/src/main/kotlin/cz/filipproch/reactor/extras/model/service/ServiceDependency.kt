package cz.filipproch.reactor.extras.model.service

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class ServiceDependency<S : IAppService>(clazz: Class<S>) {

    private val sharedObservable = AppServiceManager.observe(clazz)
            .publish()
            .replay(1)

    private val disposable: Disposable

    init {
        disposable = sharedObservable.connect()
    }

    fun observe(): Observable<S> {
        return sharedObservable
    }

    fun take(): Single<S> {
        return sharedObservable
                .take(1)
                .firstOrError()
    }

    fun use(action: (S) -> Unit) {
        observe().subscribe {
            action.invoke(it)
        }
    }

    fun dispose() {
        disposable.dispose()
    }

}