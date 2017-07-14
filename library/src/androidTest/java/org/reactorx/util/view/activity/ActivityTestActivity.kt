package org.reactorx.util.view.activity

import android.os.Bundle
import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.UiModel
import org.reactorx.ui.ReactorActivity
import org.reactorx.util.view.ReactorViewTestHelper
import org.reactorx.util.view.TestTranslator
import io.reactivex.Observable

/**
 * Simple [ReactorActivity] to be used in tests
 *
 * @author Filip Prochazka (@filipproch)
 */
class ActivityTestActivity : ReactorActivity<TestTranslator>() {

    val helper = ReactorViewTestHelper()

    /* Activity */

    override val translatorFactory: TranslatorFactory<TestTranslator>
        get() = SimpleTranslatorFactory(TestTranslator::class.java)

    override fun onUiCreated() {
        super.onUiCreated()
        methodCalled(METHOD_UI_CREATED)
    }

    override fun onUiRestored(savedInstanceState: Bundle) {
        super.onUiRestored(savedInstanceState)
        methodCalled(METHOD_UI_RESTORED)
    }

    override fun onUiReady() {
        super.onUiReady()
        methodCalled(METHOD_UI_READY)
    }

    override fun onCreateLayout() {
        methodCalled(METHOD_CREATE_LAYOUT)
    }

    override fun onEmittersInit() {
        super.onEmittersInit()
        methodCalled(ReactorViewTestHelper.METHOD_EMITTERS_INIT)
    }

    override fun onConnectModelStream(modelStream: Observable<out UiModel>) {
        super.onConnectModelStream(modelStream)
        methodCalled(ReactorViewTestHelper.METHOD_CONNECT_MODEL_STREAM)
        modelStream.consumeOnUi {
            helper.uiModelReceived(it)
        }
    }

    override fun onConnectActionStream(actionStream: Observable<out ReactorUiAction>) {
        super.onConnectActionStream(actionStream)
        methodCalled(ReactorViewTestHelper.METHOD_CONNECT_ACTION_STREAM)
        actionStream.consumeOnUi {
            helper.uiActionReceived(it)
        }
    }

    private fun methodCalled(methodName: String) {
        helper.methodCalled(methodName)
    }

    companion object {
        val METHOD_UI_CREATED = "onUiCreated"
        val METHOD_UI_RESTORED = "onUiRestored"
        val METHOD_UI_READY = "onUiReady"
        val METHOD_CREATE_LAYOUT = "onCreateLayout"
    }

}