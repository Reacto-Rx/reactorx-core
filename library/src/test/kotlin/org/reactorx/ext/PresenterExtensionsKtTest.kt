package org.reactorx.ext

import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.reactorx.state.model.Action
import org.reactorx.view.events.ViewStarted
import org.reactorx.view.events.ViewStopped

/**
 * Test cases for [org.reactorx.ext.PresenterExtensionsKt]
 */
class PresenterExtensionsKtTest {

    private val subject: PublishSubject<Action> = PublishSubject.create()
    private val receivedActions: MutableList<Action> = mutableListOf()
    private val actionsConsumer = Consumer<Action> { receivedActions.add(it) }

    @Before
    fun clearStateBeforeTest() {
        receivedActions.clear()
    }

    @Test
    fun whenViewStarted() {
        val disposable = subject.whenViewStarted()
                .subscribe(actionsConsumer)

        subject.onNext(TestAction())

        assert(receivedActions.isEmpty(), { "Action that is not ViewStarted passed trough" })

        subject.onNext(ViewStarted)

        assert(receivedActions.size == 1, { "ViewStarted action did not pass trough" })

        assert(receivedActions.first() is ViewStarted, { "Action that passed trough is not ViewStarted" })

        subject.onNext(ViewStarted)

        assert(receivedActions.size == 2, { "Second ViewStarted action did not pass trough" })

        disposable.dispose()
    }

    @Test
    fun whenViewStartedFirstTime() {
        val disposable = subject.whenViewStartedFirstTime()
                .subscribe(actionsConsumer)

        subject.onNext(TestAction())

        assert(receivedActions.isEmpty(), { "Action that is not ViewStarted passed trough" })

        subject.onNext(ViewStarted)

        assert(receivedActions.size == 1, { "ViewStarted action did not pass trough" })

        subject.onNext(ViewStarted)

        assert(receivedActions.size == 1, { "Second ViewStarted action did pass trough" })

        disposable.dispose()
    }

    @Test
    fun takeUntilViewStopped() {
        val killSubject = PublishSubject.create<Action>()
        val disposable = subject.takeUntilViewStopped(killSubject)
                .subscribe(actionsConsumer)

        subject.onNext(TestAction())

        assert(receivedActions.size == 1, { "Action did not pass trough" })

        killSubject.onNext(TestAction())
        subject.onNext(TestAction())

        assert(receivedActions.size == 2, { "Second action did not pass trough" })

        receivedActions.clear()
        killSubject.onNext(ViewStopped)
        subject.onNext(TestAction())

        assert(receivedActions.isEmpty(), { "Action passed trough after ViewStopped dispatched" })

        disposable.dispose()
    }

    private class TestAction : Action

}