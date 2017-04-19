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
interface ReactorView<out T: ReactorTranslator> {

    val translatorFactory: TranslatorFactory<T>

    fun onConnectModelChannel(modelStream: Observable<out ReactorUiModel>)

    fun onEmittersInit()

    fun registerEmitter(emitter: Observable<out ReactorUiEvent>)

    fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>)

}