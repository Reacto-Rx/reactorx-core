package cz.filipproch.reactor.util.view

import android.support.v7.app.AppCompatActivity
import cz.filipproch.reactor.base.translator.BaseReactorTranslator
import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.ui.ReactorTranslatorFragment
import cz.filipproch.reactor.ui.ReactorTranslatorHelper

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class TranslatorFragmentTestActivity : AppCompatActivity() {

    val translatorFactory = SimpleTranslatorFactory(TranslatorFragmentTestTranslator::class.java)

    var translator: TranslatorFragmentTestTranslator? = null
        private set

    var translatorFragment: ReactorTranslatorFragment<TranslatorFragmentTestTranslator>? = null
        private set

    override fun onStart() {
        super.onStart()
        translator = ReactorTranslatorHelper
                .getTranslatorFromFragment(supportFragmentManager, translatorFactory)

        translatorFragment = ReactorTranslatorHelper
                .findTranslatorFragment(supportFragmentManager)
    }

    class TranslatorFragmentTestTranslator : BaseReactorTranslator() {
        override fun onCreated() {
        }
    }

}