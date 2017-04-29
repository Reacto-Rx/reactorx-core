package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * [ReactorUiEvent] that's dispatched by some [cz.filipproch.reactor.base.view.ReactorView] implementations
 * when the [cz.filipproch.reactor.base.view.ReactorView] is paused/stopped
 *
 * @author Filip Prochazka (@filipproch)
 */
@Deprecated("Due to ambiguous name replaced with ViewResumedEvent", ReplaceWith(
        "ViewPausedEvent",
        "cz.filipproch.reactor.ui.events.ViewPausedEvent"
))
object ViewDetachedEvent : ReactorUiEvent