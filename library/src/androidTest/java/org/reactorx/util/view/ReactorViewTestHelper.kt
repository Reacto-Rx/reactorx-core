package org.reactorx.util.view

import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.UiModel
import org.reactorx.util.MethodCalled
import org.assertj.core.api.Java6Assertions

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class ReactorViewTestHelper {

    /* State variables */

    var methodCalls = mutableListOf<MethodCalled>()

    val receivedUiModels = mutableListOf<UiModel>()
    val receivedUiActions = mutableListOf<ReactorUiAction>()

    fun methodCalled(methodName: String) {
        methodCalls.add(MethodCalled(methodName, System.currentTimeMillis()))
    }

    fun uiModelReceived(model: UiModel) {
        receivedUiModels.add(model)
    }

    fun uiActionReceived(action: ReactorUiAction) {
        receivedUiActions.add(action)
    }

    fun assertMethodsCalledInOrder(vararg methodNames: String) {
        Java6Assertions.assertThat(methodCalls)
                .extracting<String> { it.methodName }
                .containsExactly(*methodNames)
    }

    companion object {
        val METHOD_EMITTERS_INIT = "onEmittersInit"
        val METHOD_CONNECT_MODEL_STREAM = "onConnectModelStream"
        val METHOD_CONNECT_ACTION_STREAM = "onConnectActionStream"
    }

}