package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * [ReactorUiEvent] that's dispatched by some [cz.filipproch.reactor.base.view.ReactorView] implementations
 * when the [cz.filipproch.reactor.base.view.ReactorView] is resumed/started
 *
 * @author Filip Prochazka (@filipproch)
 */
@Deprecated("Due to ambiguous name replaced with ViewResumedEvent", ReplaceWith(
        "ViewResumedEvent",
        "cz.filipproch.reactor.ui.events.ViewResumedEvent"
))
object ViewAttachedEvent : ReactorUiEvent