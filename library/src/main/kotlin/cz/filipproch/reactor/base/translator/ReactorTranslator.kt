package cz.filipproch.reactor.base.translator

import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import io.reactivex.Observable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
interface ReactorTranslator {

    /**
     * TODO
     */
    fun onInstanceCreated()

    /**
     * TODO
     */
    fun onBeforeInstanceDestroyed()

    /**
     * TODO
     */
    fun bindView(events: Observable<out ReactorUiEvent>)

    /**
     * TODO
     */
    fun observeUiModels(): Observable<out ReactorUiModel>

    /**
     * TODO
     */
    fun observeUiActions(): Observable<out ReactorUiAction>

    /**
     * TODO
     */
    fun unbindView()

}