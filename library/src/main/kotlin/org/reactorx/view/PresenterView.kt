package org.reactorx.view

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import org.reactorx.presenter.Presenter
import org.reactorx.presenter.PresenterFactory
import org.reactorx.view.model.UiEvent
import org.reactorx.view.util.ObservableViewUtils

/**
 * @author Filip Prochazka (@filipproch)
 */
interface PresenterView<M : Any, P : Presenter<M>> : ObservableViewUtils {

    val presenterClass: Class<P>
    val presenterFactory: PresenterFactory<P>

    val viewHelper: ViewHelper<M, P>

    val presenter: P
        get() = viewHelper.presenter

    @Deprecated("Replaced by viewModel", ReplaceWith("viewModel"))
    val lastState: M?
        get() = viewModel

    val viewModel: M?
        get() = viewHelper.viewModel

    fun connectPresenter() {
        viewHelper.connectPresenter()
        onPresenterConnected()
    }

    fun disconnectPresenter() {
        viewHelper.disconnectPresenter()
    }

    fun onPresenterConnected() {
        viewHelper.observeViewModel()
                .subscribeBy(onNext = this::onViewModelChanged)
    }

    @Deprecated("Replaced by onViewModelChanged()")
    fun onUiModel(uiModel: M) {
    }

    fun onViewModelChanged(viewModel: M) {
        onUiModel(viewModel) // todo: remove in Beta
    }

    fun observeViewModel() = viewHelper.observeViewModel()

    fun <T : Any> observeViewModelChanges(
            valueMapper: (M) -> T
    ) = viewHelper.observeViewModelChanges(valueMapper)

    fun <T : Any?> observeViewModelNullableChanges(
            valueMapper: (M) -> T
    ) = viewHelper.observeViewModelNullableChanges(valueMapper)

    fun <T : UiEvent> Observable<T>.subscribeByDispatch() {
        this.subscribeWithView(this@PresenterView::dispatch)
    }

    fun dispatch(event: UiEvent) {
        viewHelper.dispatch(event)
    }

    fun dispatch(): Consumer<UiEvent> = Consumer { dispatch(it) }

}