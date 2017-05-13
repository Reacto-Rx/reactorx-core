package cz.filipproch.reactor.util.view

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * @author Filip Prochazka (@filipproch)
 */
class TestTranslator : ReactorTranslator() {

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

        translateToModel {
            it.ofType(ReturnUiModelEvent::class.java)
                    .map { TestUiModel }
        }

        translateToAction {
            it.ofType(ReturnUiActionEvent::class.java)
                    .map { TestUiAction }
        }
    }

    override fun onBeforeDestroyed() {
        super.onBeforeDestroyed()
        onDestroyedCalled = true
    }

}