package cz.filipproch.reactor.util.view.dialogfragment

import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.ui.ReactorDialogFragment
import cz.filipproch.reactor.util.view.TestTranslator

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class TestDialogFragment : ReactorDialogFragment<TestTranslator>() {

    override val translatorFactory: TranslatorFactory<TestTranslator>
        get() = SimpleTranslatorFactory(TestTranslator::class.java)

}