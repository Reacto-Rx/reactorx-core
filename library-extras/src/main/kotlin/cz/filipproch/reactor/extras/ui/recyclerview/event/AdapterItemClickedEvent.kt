package cz.filipproch.reactor.extras.ui.recyclerview.event

import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AdapterItemClickedEvent<out T>(
        val position: Int,
        val item: T
) : ReactorUiEvent