package cz.filipproch.reactor.base.view

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
interface ReactorView<out T : ReactorTranslator> {

    /**
     * Should return a [TranslatorFactory] instance that's used to create [ReactorTranslator] instances
     */
    val translatorFactory: TranslatorFactory<T>

    /**
     * Called during [ReactorView] initialization, it's a place where you can register [ReactorUiEvent] emitters
     * using [registerEmitter] method
     */
    fun onEmittersInit()

    /**
     * Called when the stream of [ReactorUiModel] is connected from the [ReactorTranslator] to the [ReactorView]
     */
    @Deprecated("Replaced with onConnectModelStream", ReplaceWith(
            "onConnectModelStream(modelStream)"
    ))
    fun onConnectModelChannel(modelStream: Observable<out ReactorUiModel>)

    /**
     * Called when the stream of [ReactorUiModel] is connected from the [ReactorTranslator] to the [ReactorView]
     */
    fun onConnectModelStream(modelStream: Observable<out ReactorUiModel>)

    /**
     * Called when the stream of [ReactorUiAction] is connected from the [ReactorTranslator] to the [ReactorView]
     */
    @Deprecated("Replaced with onConnectActionStream", ReplaceWith(
            "onConnectActionStream(actionStream)"
    ))
    fun onConnectActionChannel(actionStream: Observable<out ReactorUiAction>)

    /**
     * Called when the stream of [ReactorUiAction] is connected from the [ReactorTranslator] to the [ReactorView]
     */
    fun onConnectActionStream(actionStream: Observable<out ReactorUiAction>)

    /**
     * Registers [emitter], so the events it emits are delivered to [ReactorTranslator]
     */
    fun registerEmitter(emitter: Observable<out ReactorUiEvent>)

    /**
     * Method to dispatch [ReactorUiEvent] directly from [ReactorView] using it's own registered emitter
     */
    fun dispatch(event: ReactorUiEvent)

    @Deprecated("Replaced with extension function consumeOnUi", ReplaceWith(
            "observable.consumeOnUi(receiverAction)"
    ))
    fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>)

    /**
     * Subscribes [receiverAction] to [Observable] on Android UI thread
     */
    fun <T> Observable<T>.consumeOnUi(receiverAction: Consumer<T>)

    /**
     * Subscribes [consumer] to [Observable] on Android UI thread,
     * using [mapper] function as [Observable.map]
     */
    fun <M : ReactorUiModel, T> Observable<M>.mapToUi(consumer: Consumer<T>, mapper: ConsumerMapper<M, T>)

}