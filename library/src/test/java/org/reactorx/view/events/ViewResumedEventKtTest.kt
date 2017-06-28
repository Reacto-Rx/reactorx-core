package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.UiEvent
import io.reactivex.Observable

/**
 * @author Filip Prochazka (@filipproch)
 */
class ViewResumedEventKtTest : BaseEventFilterTest() {
    override fun filterStream(stream: Observable<UiEvent>): Observable<out UiEvent> {
        return stream.whenViewResumed()
    }

    override fun getValidEventInstance(): UiEvent {
        return ViewResumedEvent
    }
}