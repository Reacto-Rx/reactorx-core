package org.reactorx.view

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

    lateinit var presenter: P

    lateinit var lastState: M

    private var disposable: Disposable? = null

    fun bindPresenter(activity: FragmentActivity, presenterClazz: Class<P>, factory: PresenterFactory<P>) {
        presenter = ViewModelProviders.of(activity, factory)
                .get(presenterClazz)
    }

    fun bindPresenter(fragment: Fragment, presenterClazz: Class<P>, factory: PresenterFactory<P>) {
        presenter = ViewModelProviders.of(fragment, factory)
                .get(presenterClazz)
    }

    fun onStart() {
        disposable = presenter.observeUiModel()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onNext = this::processUiModel, onError = this::processStreamError)
        presenter.uiEvents.onNext(ViewStarted)
    }

    fun onStop() {
        presenter.uiEvents.onNext(ViewStopped)
        disposable?.dispose()
    }

    private fun processUiModel(uiModel: M) {
        this.lastState = uiModel
        uiModelCallback.invoke(uiModel)
    }

    private fun processStreamError(throwable: Throwable) {
        errorCallback?.invoke(throwable)
    }

}