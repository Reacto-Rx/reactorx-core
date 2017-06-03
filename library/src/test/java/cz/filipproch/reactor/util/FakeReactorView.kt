package cz.filipproch.reactor.util

import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.base.view.ReactorView
import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class FakeReactorView : ReactorView<TestTranslator> {

    var onEmittersInitCalled = false
    var onConnectModelChannelCalled = false
    var onConnectModelStreamCalled = false
    var onConnectActionChannelCalled = false
    var onConnectActionStreamCalled = false

    var registerEmitterExecutions = 0
    var dispatchExecutions = 0
    var consumeOnUiExecutions = 0

    private var onEmittersInitCallback: (() -> Unit)? = null

    override val translatorFactory: TranslatorFactory<TestTranslator>
        get() = SimpleTranslatorFactory(TestTranslator::class.java)

    override fun onEmittersInit() {
        onEmittersInitCalled = true
        onEmittersInitCallback?.invoke()
    }

    override fun onConnectModelStream(modelStream: Observable<out ReactorUiModel>) {
        onConnectModelStreamCalled = true
    }

    override fun onConnectActionStream(actionStream: Observable<out ReactorUiAction>) {
        onConnectActionStreamCalled = true
    }

    override fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        registerEmitterExecutions++
    }

    override fun dispatch(event: ReactorUiEvent) {
        dispatchExecutions++
    }

    override fun <T> Observable<T>.consumeOnUi(receiverAction: Consumer<T>) {
        consumeOnUiExecutions++
    }

    fun setOnEmittersInitCallback(callback: () -> Unit) {
        onEmittersInitCallback = callback
    }
}