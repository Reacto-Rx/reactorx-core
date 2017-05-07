package cz.filipproch.reactor.ui.events

import android.os.Bundle
import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
@Deprecated("Removed due to ambiguous name", ReplaceWith(
        "ViewCreatedEvent"
))
data class ViewRestoredEvent(val bundle: Bundle) : ReactorUiEvent