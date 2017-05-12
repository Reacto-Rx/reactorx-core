package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewDestroyedEventKtTest : BaseEventFilterTest() {

    override fun getValidEventInstance(): ReactorUiEvent {
        return ViewDestroyedEvent
    }

    override fun filterStream(stream: Observable<ReactorUiEvent>): Observable<out ReactorUiEvent> {
        return stream.whenViewDestroyed()
    }

}