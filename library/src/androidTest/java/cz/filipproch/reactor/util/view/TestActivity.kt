package cz.filipproch.reactor.util.view

import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.ui.CompatReactorActivity

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class TestActivity : CompatReactorActivity<TestActivityTranslator>() {
    override val translatorFactory: TranslatorFactory<TestActivityTranslator>
        get() = SimpleTranslatorFactory(TestActivityTranslator::class.java)

    override fun onCreateLayout() {
    }

}