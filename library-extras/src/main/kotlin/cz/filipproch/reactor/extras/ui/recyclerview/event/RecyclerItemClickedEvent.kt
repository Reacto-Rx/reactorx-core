package cz.filipproch.reactor.extras.ui.recyclerview.event

import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class RecyclerItemClickedEvent(val position: Int, val long: Boolean): ReactorUiEvent {
    companion object {
        fun clicked(position: Int): RecyclerItemClickedEvent {
            return RecyclerItemClickedEvent(position, false)
        }

        fun longClicked(position: Int): RecyclerItemClickedEvent {
            return RecyclerItemClickedEvent(position, true)
        }
    }
}