package org.reactorx.util

import cz.filipproch.reactor.base.view.UiEvent
import org.reactorx.util.view.TestTranslator
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Java6Assertions

fun <T : TestTranslator?> assertThatTranslator(translator: T): ReactorTranslatorAssert {
    return ReactorTranslatorAssert(translator)
}

class ReactorTranslatorAssert(translator: TestTranslator?) :
        AbstractAssert<ReactorTranslatorAssert, TestTranslator>(translator, ReactorTranslatorAssert::class.java) {

    fun receivedFollowingEventsInOrder(
            vararg eventClazz: Class<out UiEvent>
    ) {
        Java6Assertions.assertThat(this.actual.receivedEvents)
                .extracting<Class<out UiEvent>> { it.javaClass }
                .containsExactly(*eventClazz)
    }

}