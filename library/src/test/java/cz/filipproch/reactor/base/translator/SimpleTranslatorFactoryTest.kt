package cz.filipproch.reactor.base.translator

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


/**
 * TODO: add description

 * @author Filip Prochazka (@filipproch)
 */
class SimpleTranslatorFactoryTest {

    @Test
    fun testCreate() {
        val simpleFactory = SimpleTranslatorFactory(SimpleTranslator::class.java)

        val translatorInstance = simpleFactory.create()

        assertThat(translatorInstance).isInstanceOf(SimpleTranslator::class.java)
    }

    @Test(expected = RuntimeException::class)
    fun testCreateTranslatorWithoutPublicConstructor() {
        val simpleFactory = SimpleTranslatorFactory(SimpleBrokenTranslator::class.java)

        simpleFactory.create()
    }

    @Test(expected = RuntimeException::class)
    fun testCreateAbstractTranslator() {
        val simpleFactory = SimpleTranslatorFactory(SimpleAbstractTranslator::class.java)

        simpleFactory.create()
    }

    private class SimpleTranslator : ReactorTranslator() {
        override fun onCreated() {
        }
    }

    private class SimpleBrokenTranslator private constructor() : ReactorTranslator() {
        override fun onCreated() {
        }
    }

    private abstract class SimpleAbstractTranslator : ReactorTranslator() {
        override fun onCreated() {
        }
    }

}