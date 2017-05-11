package cz.filipproch.reactor.util.view

import cz.filipproch.reactor.base.translator.BaseReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class TestActivityTranslator : BaseReactorTranslator() {

    val receivedEvents = mutableListOf<ReactorUiEvent>()

    override fun onCreated() {
        reactTo {
            it.subscribe { receivedEvents.add(it) }
        }
    }

}