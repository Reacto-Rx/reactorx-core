package cz.filipproch.reactor.ui.events

import cz.filipproch.reactor.base.view.UiEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class BaseEventFilterTest {

    val allReceivedEvents = mutableListOf<UiEvent>()
    val validReceivedEvents = mutableListOf<UiEvent>()

    lateinit var subject: PublishSubject<UiEvent>

    @Before
    fun prepareTests() {
        subject = PublishSubject.create<UiEvent>()

        subject.subscribe { allReceivedEvents.add(it) }

        filterStream(subject).subscribe { validReceivedEvents.add(it) }
    }

    @Test
    fun runFilterTest() {
        subject.onNext(TestEvent)

        Assertions.assertThat(allReceivedEvents).hasSize(1)
        Assertions.assertThat(validReceivedEvents).hasSize(0)

        subject.onNext(getValidEventInstance())

        Assertions.assertThat(allReceivedEvents).hasSize(2)
        Assertions.assertThat(validReceivedEvents).hasSize(1)
    }

    abstract fun filterStream(stream: Observable<UiEvent>): Observable<out UiEvent>

    abstract fun getValidEventInstance(): UiEvent

}