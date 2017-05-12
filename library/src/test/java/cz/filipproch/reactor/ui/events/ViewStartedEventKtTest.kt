package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewStartedEventKtTest : BaseEventFilterTest() {
    override fun filterStream(stream: Observable<ReactorUiEvent>): Observable<out ReactorUiEvent> {
        return stream.whenViewStarted()
    }

    override fun getValidEventInstance(): ReactorUiEvent {
        return ViewStartedEvent
    }
}