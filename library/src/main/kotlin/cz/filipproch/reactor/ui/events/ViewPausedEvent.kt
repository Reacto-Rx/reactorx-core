package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * [ReactorUiEvent] that's dispatched by some [cz.filipproch.reactor.base.view.ReactorView] implementations
 * when the [cz.filipproch.reactor.base.view.ReactorView] is paused
 *
 * @author Filip Prochazka (@filipproch)
 */
object ViewPausedEvent : ReactorUiEvent