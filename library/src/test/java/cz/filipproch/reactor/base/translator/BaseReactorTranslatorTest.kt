package cz.filipproch.reactor.base.translator

import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Tests for [BaseReactorTranslator]
 *
 * @author Filip Prochazka (@filipproch)
 */
class BaseReactorTranslatorTest {

    private lateinit var translator: TestReactorTranslator

    @Before
    fun resetTranslatorInstance() {
        translator = TestReactorTranslator()
        translator.onInstanceCreated()
    }

    @Test
    fun testOnInstanceCreated() {
        translator = TestReactorTranslator()

        assertThat(translator.isCreated).isFalse()

        translator.onInstanceCreated()

        assertThat(translator.isCreated).isTrue()

        assertThat(translator.onCreatedCalled).isTrue()
    }

    @Test
    fun testOnBeforeInstanceDestroyed() {
        assertThat(translator.isDestroyed).isFalse()

        translator.onBeforeInstanceDestroyed()

        assertThat(translator.isDestroyed).isTrue()

        assertThat(translator.onBeforeDestroyedCalled).isTrue()
    }

    /**
     * Note: this test depends on [BaseReactorTranslator.reactTo] to work properly
     */
    @Test
    fun bindView() {
        val eventEmitter = PublishSubject.create<ReactorUiEvent>()

        // execute the method
        translator.bindView(eventEmitter)

        // test that events pass trough
        val event = TestReactorUiEvent()

        eventEmitter.onNext(event)

        // event received
        assertThat(translator.lastEvent).isNotNull()

        // it's the same event
        assertThat(translator.lastEvent).isEqualTo(event)
    }

    @Test
    fun unbindView() {
        val eventEmitter = PublishSubject.create<ReactorUiEvent>()

        // bind the view
        translator.bindView(eventEmitter)

        // execute the method
        translator.unbindView()

        val event = TestReactorUiEvent()
        eventEmitter.onNext(event)

        assertThat(translator.lastEvent).isNull()
    }

    @Test
    fun translateToModel() {
        val eventEmitter = bindEmitterToTranslator()

        var lastModel: ReactorUiModel? = null

        translator.observeUiModels()
                .subscribe { lastModel = it }

        val event = TestReactorUiEvent()
        eventEmitter.onNext(event)

        assertThat(lastModel).isNotNull()

        assertThat(lastModel).isInstanceOf(TestReactorUiModel::class.java)
    }

    @Test
    fun translateToAction() {
        val eventEmitter = bindEmitterToTranslator()

        var lastAction: ReactorUiAction? = null

        // bind event emissions
        translator.observeUiActions()
                .subscribe { lastAction = it }

        val event = TestReactorUiEvent()
        eventEmitter.onNext(event)

        assertThat(lastAction).isNotNull()

        assertThat(lastAction).isInstanceOf(TestReactorUiAction::class.java)
    }

    @Test
    fun reactTo() {
        val eventEmitter = bindEmitterToTranslator()

        val event = TestReactorUiEvent()
        eventEmitter.onNext(event)

        assertThat(translator.lastEvent).isNotNull()

        assertThat(translator.lastEvent).isEqualTo(event)
    }

    private fun bindEmitterToTranslator(): PublishSubject<ReactorUiEvent> {
        val eventEmitter = PublishSubject.create<ReactorUiEvent>()
        translator.bindView(eventEmitter)
        return eventEmitter
    }

    private class TestReactorTranslator : BaseReactorTranslator() {

        var onCreatedCalled: Boolean = false
        var onBeforeDestroyedCalled: Boolean = false

        var lastEvent: ReactorUiEvent? = null

        override fun onCreated() {
            onCreatedCalled = true

            reactTo {
                it.subscribe { lastEvent = it }
            }

            translateToModel {
                it.map { TestReactorUiModel() }
            }

            translateToAction {
                it.map { TestReactorUiAction() }
            }
        }

        override fun onBeforeDestroyed() {
            super.onBeforeDestroyed()
            onBeforeDestroyedCalled = true
        }
    }

    private class TestReactorUiEvent : ReactorUiEvent

    private class TestReactorUiModel : ReactorUiModel {
        override fun getType(): Class<*> {
            return TestReactorUiModel::class.java
        }
    }

    private class TestReactorUiAction : ReactorUiAction

}