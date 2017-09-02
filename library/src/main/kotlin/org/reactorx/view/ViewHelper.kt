package org.reactorx.view

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.reactorx.presenter.Presenter
import org.reactorx.presenter.PresenterFactory
import org.reactorx.view.events.ViewStarted
import org.reactorx.view.events.ViewStopped

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewHelper<M : Any, P : Presenter<M>>(
        private val uiModelCallback: (M) -> Unit,
        private val errorCallback: ((Throwable) -> Unit)? = null
) {

    val presenter: P
        get() = presenterInstance ?: throw IllegalStateException("presenter not instantiated")

    val hasPresenterInstance: Boolean get() = presenterInstance != null
    var lastState: M? = null

    private var presenterInstance: P? = null

    private var disposable: Disposable? = null

    fun restorePresenterInstance(
            activity: FragmentActivity,
            presenterClazz: Class<P>,
            factory: PresenterFactory<P>
    ) {
        restorePresenterInstance(
                viewModelProvider = ViewModelProviders.of(activity, factory),
                presenterClazz = presenterClazz
        )
    }

    @Deprecated("Replaced by restorePresenterInstance()", ReplaceWith("restorePresenterInstance(activity, presenterClazz, factory)"))
    fun bindPresenter(
            activity: FragmentActivity,
            presenterClazz: Class<P>,
            factory: PresenterFactory<P>
    ) {
        restorePresenterInstance(activity, presenterClazz, factory)
    }

    fun restorePresenterInstance(
            fragment: Fragment,
            presenterClazz: Class<P>,
            factory: PresenterFactory<P>
    ) {
        restorePresenterInstance(
                viewModelProvider = ViewModelProviders.of(fragment, factory),
                presenterClazz = presenterClazz
        )
    }

    @Deprecated("Replaced by restorePresenterInstance()", ReplaceWith("restorePresenterInstance(fragment, presenterClazz, factory)"))
    fun bindPresenter(
            fragment: Fragment,
            presenterClazz: Class<P>,
            factory: PresenterFactory<P>
    ) {
        restorePresenterInstance(fragment, presenterClazz, factory)
    }

    fun restorePresenterInstance(
            viewModelProvider: ViewModelProvider,
            presenterClazz: Class<P>
    ) {
        presenterInstance = viewModelProvider.get(presenterClazz)
    }

    fun instantiatePresenter(
            factory: PresenterFactory<P>
    ) {
        presenterInstance = factory.newInstance()
    }

    fun destroyPresenter() {
        presenterInstance?.destroySelf()
        presenterInstance = null
    }

    fun connectPresenter() {
        if (!hasPresenterInstance) {
            throw IllegalStateException("ViewHelper was disposed by destroyPresenter()")
        }

        disposable = presenter.observeUiModel()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = this::processUiModel, onError = this::processStreamError)
        presenter.dispatch(ViewStarted)
    }

    @Deprecated("Replaced with connectPresenter()", ReplaceWith("connectPresenter()"))
    fun onStart() {
        connectPresenter()
    }

    fun disconnectPresenter() {
        if (!hasPresenterInstance) {
            throw IllegalStateException("ViewHelper was disposed by destroyPresenter()")
        }

        presenter.dispatch(ViewStopped)
        disposable?.dispose()
    }

    @Deprecated("Replaced with disconnectPresenter()", ReplaceWith("disconnectPresenter()"))
    fun onStop() {
        disconnectPresenter()
    }

    private fun processUiModel(uiModel: M) {
        this.lastState = uiModel
        uiModelCallback.invoke(uiModel)
    }

    private fun processStreamError(throwable: Throwable) {
        errorCallback?.invoke(throwable)
    }

}