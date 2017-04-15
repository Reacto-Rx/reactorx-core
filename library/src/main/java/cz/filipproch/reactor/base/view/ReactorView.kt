package cz.filipproch.reactor.base.view

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.ui.model.ReceiverAction
import io.reactivex.Observable

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

    fun <T : ReactorUiModel> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: ReceiverAction<T>)

}