package cz.filipproch.reactor.base.translator

import org.junit.Test

import org.assertj.core.api.Assertions.assertThat


/**
 * TODO: add description

 * @author Filip Prochazka (@filipproch)
 */
class SimpleTranslatorFactoryTest {

    @Test
    fun create() {
        val simpleFactory = SimpleTranslatorFactory(SimpleTranslator::class.java)

        val translatorInstance = simpleFactory.create()

        assertThat(translatorInstance).isInstanceOf(SimpleTranslator::class.java)
    }

    private class SimpleTranslator : BaseReactorTranslator() {
        override fun onCreated() {
        }
    }

}