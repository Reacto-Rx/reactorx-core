package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.ReactorUiEvent
import io.reactivex.Observable

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewCreatedEventKtTest : BaseEventFilterTest() {

    override fun getValidEventInstance(): ReactorUiEvent {
        return ViewCreatedEvent(null)
    }

    override fun filterStream(stream: Observable<ReactorUiEvent>): Observable<out ReactorUiEvent> {
        return stream.whenViewCreated()
    }

}