package org.reactorx.util

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.UiEvent

/**
 * @author Filip Prochazka (@filipproch)
 */
class TestTranslator : ReactorTranslator() {

    var onCreatedCalled = false
    var onDestroyedCalled = false

    val receivedEvents = mutableListOf<UiEvent>()

    override fun onCreated() {
        onCreatedCalled = true

        reactTo {
            subscribe {
                receivedEvents.add(it)
            }
        }
    }

    override fun onBeforeDestroyed() {
        super.onBeforeDestroyed()
        onDestroyedCalled = true
    }

}