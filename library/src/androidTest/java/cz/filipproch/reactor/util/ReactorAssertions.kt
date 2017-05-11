package cz.filipproch.reactor.util

import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.util.view.TestTranslator
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Java6Assertions

fun <T : TestTranslator?> assertThatTranslator(translator: T): ReactorTranslatorAssert {
    return ReactorTranslatorAssert(translator)
}

class ReactorTranslatorAssert(translator: TestTranslator?) :
        AbstractAssert<ReactorTranslatorAssert, TestTranslator>(translator, ReactorTranslatorAssert::class.java) {

    fun receivedFollowingEventsInOrder(
            vararg eventClazz: Class<out ReactorUiEvent>
    ) {
        Java6Assertions.assertThat(this.actual).isNotNull()

        Java6Assertions.assertThat(this.actual.receivedEvents)
                .extracting<Class<out ReactorUiEvent>> { it.javaClass }
                .containsExactly(*eventClazz)
    }

}