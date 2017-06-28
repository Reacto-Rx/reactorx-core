package cz.filipproch.reactor.extras.ui.recyclerview.event

import cz.filipproch.reactor.base.view.UiEvent

/**
 * TODO: add description
 */
data class AdapterItemClickedEvent<out T>(
        val position: Int,
        val item: T
) : UiEvent