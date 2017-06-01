package cz.filipproch.reactor.base.view

import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * This is the interface representation of the <b>View</b> concept in
 * the <b>Reactor</b> library
 */
interface ReactorView<out T : IReactorTranslator> {

    /**
     * Must return a [TranslatorFactory] instance that's used to
     * create [IReactorTranslator] instances as needed.
     */
    val translatorFactory: TranslatorFactory<T>

    /**
     * Called during [ReactorView] initialization, it's the only place
     * where you can register [ReactorUiEvent] emitters
     * using the [registerEmitter] method
     */
    fun onEmittersInit()

    /**
     * Called when the stream of [ReactorUiModel] is connected
     * to this [ReactorView]
     */
    fun onConnectModelStream(modelStream: Observable<out ReactorUiModel>)

    /**
     * Called when the stream of [ReactorUiAction] is connected
     * to this [ReactorView]
     */
    fun onConnectActionStream(actionStream: Observable<out ReactorUiAction>)

    /**
     * Registers [emitter] stream, so that the events
     * it emits are delivered to the bound [IReactorTranslator]
     */
    fun registerEmitter(emitter: Observable<out ReactorUiEvent>)

    /**
     * Method to dispatch [ReactorUiEvent] directly from [ReactorView]
     * using it's own registered emitter
     */
    fun dispatch(event: ReactorUiEvent)

    /**
     * Subscribes [receiverAction] to [Observable] on Android UI thread
     */
    fun <T> Observable<T>.consumeOnUi(receiverAction: Consumer<T>)

}