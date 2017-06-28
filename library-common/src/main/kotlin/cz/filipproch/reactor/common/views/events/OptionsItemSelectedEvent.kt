package cz.filipproch.reactor.common.views.events

import android.view.MenuItem
import cz.filipproch.reactor.base.view.UiEvent

/**
 * TODO: add description
 */
data class OptionsItemSelectedEvent(
        val menuItem: MenuItem
) : UiEvent