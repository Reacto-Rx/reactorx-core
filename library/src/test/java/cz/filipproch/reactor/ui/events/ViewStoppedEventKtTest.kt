package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewStoppedEventKtTest : BaseEventFilterTest() {
    override fun filterStream(stream: Observable<ReactorUiEvent>): Observable<out ReactorUiEvent> {
        return stream.whenViewStopped()
    }

    override fun getValidEventInstance(): ReactorUiEvent {
        return ViewStoppedEvent
    }
}