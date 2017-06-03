package cz.filipproch.reactor.ui

import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.ui.events.TestEvent
import cz.filipproch.reactor.util.FakeReactorView
import cz.filipproch.reactor.util.TestTranslator
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * @author Filip Prochazka (@filipproch)
 */
class ReactorViewHelperTest {

    private var reactorView: FakeReactorView? = null
    private var viewHelper: ReactorViewHelper<TestTranslator>? = null

    @Before
    fun prepareViewHelper() {
        reactorView = FakeReactorView()
        viewHelper = ReactorViewHelper(checkNotNull(reactorView))
    }

    @Test
    fun onReadyToRegisterEmittersTest() {
        assertThat(reactorView?.onEmittersInitCalled).isFalse()

        viewHelper?.onReadyToRegisterEmitters()

        assertThat(reactorView?.onEmittersInitCalled).isTrue()
    }

    @Test
    fun registerEmitterTest() {
        var emitterSubscribed = false
        val emitter = Observable.create<ReactorUiEvent> {
            emitterSubscribed = true
            it.onComplete()
        }

        reactorView?.setOnEmittersInitCallback {
            viewHelper?.registerEmitter(emitter)
        }

        viewHelper?.onReadyToRegisterEmitters()

        assertThat(emitterSubscribed).isTrue()
    }

    @Test(expected = ReactorViewHelper.IllegalLifecycleOperation::class)
    fun registerEmitterAtInvalidTimeTest() {
        val emitter = Observable.create<ReactorUiEvent> {
            it.onComplete()
        }

        viewHelper?.registerEmitter(emitter)
    }

    @Test
    fun translatorUnbindViewCalledInOnViewNotUsable() {
        // prepare for the test
        val emitter = PublishSubject.create<ReactorUiEvent>()
        reactorView?.setOnEmittersInitCallback {
            viewHelper?.registerEmitter(emitter)
        }
        viewHelper?.onReadyToRegisterEmitters()

        val translator = TestTranslator()
        translator.onCreated()

        viewHelper?.bindTranslatorWithView(translator)

        // the test

        // emit an event
        emitter.onNext(TestEvent)

        // it should arrive, alone
        assertThat(translator.receivedEvents).hasSize(1)

        // clear all received events
        translator.receivedEvents.clear()

        viewHelper?.onViewNotUsable()

        // emit an event
        emitter.onNext(TestEvent)

        assertThat(translator.receivedEvents).hasSize(0)

        // rebind view
        viewHelper?.bindTranslatorWithView(translator)

        // emit an event
        emitter.onNext(TestEvent)

        // single event should arrive
        assertThat(translator.receivedEvents).hasSize(2)
    }

    @Test
    fun eventsAreDeliveredToTranslatorAfterUnbindAndRebind() {
        // prepare for the test
        val emitter = PublishSubject.create<ReactorUiEvent>()
        reactorView?.setOnEmittersInitCallback {
            viewHelper?.registerEmitter(emitter)
        }
        viewHelper?.onReadyToRegisterEmitters()

        val translator = TestTranslator()
        translator.onCreated()

        viewHelper?.bindTranslatorWithView(translator)

        // the test

        viewHelper?.onViewNotUsable()

        // emit an event
        emitter.onNext(TestEvent)

        viewHelper?.bindTranslatorWithView(translator)

        // single event should arrive
        assertThat(translator.receivedEvents).hasSize(1)
    }

}