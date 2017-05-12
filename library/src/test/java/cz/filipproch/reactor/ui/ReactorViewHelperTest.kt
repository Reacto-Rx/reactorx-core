package cz.filipproch.reactor.ui

import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.util.FakeReactorView
import cz.filipproch.reactor.util.TestTranslator
import io.reactivex.Observable
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

}