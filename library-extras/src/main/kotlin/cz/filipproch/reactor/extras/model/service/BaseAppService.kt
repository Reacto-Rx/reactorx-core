package cz.filipproch.reactor.extras.model.service

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class BaseAppService : IAppService {

    private val instanceDisposable = CompositeDisposable()

    protected fun disposeWithInstance(inlineFunction: () -> Disposable) {
        instanceDisposable.add(inlineFunction.invoke())
    }

    override fun dispose() {
        instanceDisposable.dispose()
    }

}