package cz.filipproch.reactor.base.translator

import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.UiEvent
import cz.filipproch.reactor.base.view.UiModel
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Tests for [ReactorTranslator]
 *
 * @author Filip Prochazka (@filipproch)
 */
class ReactorTranslatorTest {

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
     * Note: this test depends on [ReactorTranslator.reactTo] to work properly
     */
    @Test
    fun bindView() {
        val eventEmitter = PublishSubject.create<UiEvent>()

        // execute the method
        translator.bindView(eventEmitter)

        // test that events pass trough
        val event = TestUiEvent()

        eventEmitter.onNext(event)

        // event received
        assertThat(translator.lastEvent).isNotNull()

        // it's the same event
        assertThat(translator.lastEvent).isEqualTo(event)
    }

    @Test
    fun unbindView() {
        val eventEmitter = PublishSubject.create<UiEvent>()

        // bind the view
        translator.bindView(eventEmitter)

        // execute the method
        translator.unbindView()

        val event = TestUiEvent()
        eventEmitter.onNext(event)

        assertThat(translator.lastEvent).isNull()
    }

    @Test
    fun translateToModel() {
        val eventEmitter = bindEmitterToTranslator()

        val receivedModels = mutableListOf<UiModel>()

        translator.observeUiModels()
                .subscribe { receivedModels.add(it) }

        val event = TestUiEvent()
        eventEmitter.onNext(event)

        assertThat(receivedModels).hasSize(1)

        assertThat(receivedModels.first()).isInstanceOf(TestUiModel::class.java)
    }

    @Test
    fun translateToAction() {
        val eventEmitter = bindEmitterToTranslator()

        val receivedActions = mutableListOf<ReactorUiAction>()

        // bind event emissions
        translator.observeUiActions()
                .subscribe { receivedActions.add(it) }

        val event = TestUiEvent()
        eventEmitter.onNext(event)

        assertThat(receivedActions).hasSize(1)

        assertThat(receivedActions.first()).isInstanceOf(TestReactorUiAction::class.java)
    }

    @Test
    fun reactTo() {
        val eventEmitter = bindEmitterToTranslator()

        val event = TestUiEvent()
        eventEmitter.onNext(event)

        assertThat(translator.lastEvent).isNotNull()

        assertThat(translator.lastEvent).isEqualTo(event)
    }

    private fun bindEmitterToTranslator(): PublishSubject<UiEvent> {
        val eventEmitter = PublishSubject.create<UiEvent>()
        translator.bindView(eventEmitter)
        return eventEmitter
    }

    private class TestReactorTranslator : ReactorTranslator() {

        var onCreatedCalled: Boolean = false
        var onBeforeDestroyedCalled: Boolean = false

        var lastEvent: UiEvent? = null

        override fun onCreated() {
            onCreatedCalled = true

            reactTo {
                subscribe { lastEvent = it }
            }

            translateToModel {
                map { TestUiModel() }
            }

            translateToAction {
                map { TestReactorUiAction() }
            }
        }

        override fun onBeforeDestroyed() {
            super.onBeforeDestroyed()
            onBeforeDestroyedCalled = true
        }
    }

    private class TestUiEvent : UiEvent

    private class TestUiModel : UiModel {
        override fun getType(): Class<*> {
            return TestUiModel::class.java
        }
    }

    private class TestReactorUiAction : ReactorUiAction

}