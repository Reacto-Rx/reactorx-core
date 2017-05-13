package cz.filipproch.reactor.base.translator

import android.support.annotation.NonNull
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import io.reactivex.Observable

/**
 * This is the interface representation of the <b>Translator</b> concept in the <b>Reactor</b> library.
 */
interface IReactorTranslator {

    /**
     * Called when the new instance of this [IReactorTranslator] was created
     */
    fun onInstanceCreated()

    /**
     * Called before the instance is destroyed and thrown away
     */
    fun onBeforeInstanceDestroyed()

    /**
     * Called by [cz.filipproch.reactor.base.view.ReactorView] to bind
     * it's stream of [ReactorUiEvent]s to this [IReactorTranslator] instance
     */
    fun bindView(@NonNull events: Observable<out ReactorUiEvent>)

    /**
     * Called by [cz.filipproch.reactor.base.view.ReactorView] to notify the
     * [IReactorTranslator] that it's going to be destroyed. Should be used to dispose
     * connected streams.
     */
    fun unbindView()

    /**
     * Returns stream of [ReactorUiModel]s for the [cz.filipproch.reactor.base.view.ReactorView]
     */
    fun observeUiModels(): Observable<out ReactorUiModel>

    /**
     * Returns stream of [ReactorUiAction]s for the [cz.filipproch.reactor.base.view.ReactorView]
     */
    fun observeUiActions(): Observable<out ReactorUiAction>

}