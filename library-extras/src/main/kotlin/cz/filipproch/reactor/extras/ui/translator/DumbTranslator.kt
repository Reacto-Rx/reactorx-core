package cz.filipproch.reactor.extras.ui.translator

import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import io.reactivex.Observable

/**
 * A [IReactorTranslator] implementation that does nothing
 *
 * Can be used when you need to use [cz.filipproch.reactor.base.view.ReactorView]
 * implementation but don't want to implement [IReactorTranslator] for it
 */
class DumbTranslator : IReactorTranslator {

    override fun onInstanceCreated() {
    }

    override fun onBeforeInstanceDestroyed() {
    }

    override fun bindView(events: Observable<out ReactorUiEvent>) {
    }

    override fun observeUiModels(): Observable<out ReactorUiModel> {
        return Observable.empty()
    }

    override fun observeUiActions(): Observable<out ReactorUiAction> {
        return Observable.empty()
    }

    override fun unbindView() {
    }
}