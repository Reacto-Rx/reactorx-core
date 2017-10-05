package org.reactorx.view

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.reactorx.presenter.Presenter
import org.reactorx.presenter.PresenterFactory
import org.reactorx.view.events.ViewStarted
import org.reactorx.view.events.ViewStopped
import org.reactorx.view.model.UiEvent
import org.reactorx.view.util.NullableValue
import org.reactorx.view.util.UiEventBuffer

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewHelper<M : Any, P : Presenter<M>> : UiEventBuffer {

    val presenter: P
        get() = presenterInstance ?: throw IllegalStateException("presenter not instantiated")

    val hasPresenterInstance: Boolean get() = presenterInstance != null

    override var isPresenterConnected: Boolean = false
        private set

    @Deprecated("Replaced by viewModel", ReplaceWith("viewModel"))
    val lastState: M?
        get() = viewModel

    var viewModel: M? = null

    private var presenterInstance: P? = null

    private var sharedUiModelObservable: Observable<M>? = null
    private var disposable: Disposable? = null

    override val bufferedEvents: MutableList<UiEvent> = mutableListOf()

    /*
        Instance methods
     */

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
        presenterInstance = factory.newInstance().apply {
            onPostCreated()
        }
    }

    fun destroyPresenter() {
        presenterInstance?.destroySelf()
        presenterInstance = null
    }

    /*
        Connection methods
     */

    fun connectPresenter() {
        if (!hasPresenterInstance) {
            throw IllegalStateException("ViewHelper was disposed by destroyPresenter()")
        }

        presenter.observeUiModel()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { this.viewModel = it } // update viewModel variable
                .publish()
                .let { observable ->
                    sharedUiModelObservable = observable

                    disposable = observable.connect()
                }

        updatePresenterConnected(true)

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

        dispatch(ViewStopped)

        updatePresenterConnected(false)

        sharedUiModelObservable = null
        disposable?.dispose()
    }

    @Deprecated("Replaced with disconnectPresenter()", ReplaceWith("disconnectPresenter()"))
    fun onStop() {
        disconnectPresenter()
    }

    /*
        ViewModel helpers
     */

    fun observeViewModel(): Observable<M> {
        if (!hasPresenterInstance) {
            throw IllegalStateException("ViewHelper was disposed by destroyPresenter()")
        }

        return sharedUiModelObservable
                ?: throw IllegalStateException("ViewHelper is not connected with presenter")
    }

    fun <T : Any> observeViewModelChanges(
            valueMapper: (M) -> T
    ): Observable<T> {
        return observeViewModel()
                .map(valueMapper)
                .distinctUntilChanged()
    }

    fun <T : Any?> observeViewModelNullableChanges(
            valueMapper: (M) -> T
    ): Observable<NullableValue<T>> {
        return observeViewModel()
                .map {
                    NullableValue(value = valueMapper.invoke(it))
                }
                .distinctUntilChanged()
    }

    /*
        UiEvents helpers
     */

    override fun dispatch(event: UiEvent) {
        if (isPresenterConnected) {
            presenter.dispatch(event)
        } else {
            bufferEvent(event)
        }
    }

    private fun updatePresenterConnected(connected: Boolean) {
        this.isPresenterConnected = connected
        if (connected) {
            dispatchAllBufferedEvents()
        }
    }

}