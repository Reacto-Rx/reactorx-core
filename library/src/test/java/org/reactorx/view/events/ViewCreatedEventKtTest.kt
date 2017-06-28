package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.UiEvent
import io.reactivex.Observable

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewCreatedEventKtTest : BaseEventFilterTest() {

    override fun getValidEventInstance(): UiEvent {
        return ViewCreatedEvent(null)
    }

    override fun filterStream(stream: Observable<UiEvent>): Observable<out UiEvent> {
        return stream.whenViewCreated()
    }

}