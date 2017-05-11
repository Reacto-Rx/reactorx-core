package cz.filipproch.reactor.util.view

import cz.filipproch.reactor.base.translator.BaseReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class TestTranslator : BaseReactorTranslator() {

    var onCreatedCalled = false
    var onDestroyedCalled = false

    val receivedEvents = mutableListOf<ReactorUiEvent>()

    override fun onCreated() {
        onCreatedCalled = true

        reactTo {
            it.subscribe {
                receivedEvents.add(it)
            }
        }
    }

    override fun onBeforeDestroyed() {
        super.onBeforeDestroyed()
        onDestroyedCalled = true
    }

}