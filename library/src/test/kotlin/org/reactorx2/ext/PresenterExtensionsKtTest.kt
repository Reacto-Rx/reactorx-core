package org.reactorx2.ext

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.reactorx2.state.model.Action
import org.reactorx2.view.events.ViewStarted
import org.reactorx2.view.events.ViewStopped

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
        subject.whenViewStarted()
                .whileSubscribed {
                    subject.onNext(TestAction())

                    assert(receivedActions.isEmpty(), { "Action that is not ViewStarted passed trough" })

                    subject.onNext(ViewStarted)

                    assert(receivedActions.size == 1, { "ViewStarted action did not pass trough" })

                    assert(receivedActions.first() is ViewStarted, { "Action that passed trough is not ViewStarted" })

                    subject.onNext(ViewStarted)

                    assert(receivedActions.size == 2, { "Second ViewStarted action did not pass trough" })
                }
    }

    @Test
    fun whenViewStartedFirstTime() {
        subject.whenViewStartedFirstTime()
                .whileSubscribed {
                    subject.onNext(TestAction())

                    assert(receivedActions.isEmpty(), { "Action that is not ViewStarted passed trough" })

                    subject.onNext(ViewStarted)

                    assert(receivedActions.size == 1, { "ViewStarted action did not pass trough" })

                    subject.onNext(ViewStarted)

                    assert(receivedActions.size == 1, { "Second ViewStarted action did pass trough" })
                }
    }

    @Test
    fun takeUntilViewStopped() {
        val killSubject = PublishSubject.create<Action>()
        subject.takeUntilViewStopped(killSubject)
                .whileSubscribed {
                    subject.onNext(TestAction())

                    assert(receivedActions.size == 1, { "Action did not pass trough" })

                    killSubject.onNext(TestAction())
                    subject.onNext(TestAction())

                    assert(receivedActions.size == 2, { "Second action did not pass trough" })

                    receivedActions.clear()
                    killSubject.onNext(ViewStopped)
                    subject.onNext(TestAction())

                    assert(receivedActions.isEmpty(), { "Action passed trough after ViewStopped dispatched" })
                }
    }

    private inline fun Observable<out Action>.whileSubscribed(
            body: () -> Unit
    ) {
        val disposable = subscribe(actionsConsumer)

        body.invoke()

        disposable.dispose()
    }

    private class TestAction : Action

}