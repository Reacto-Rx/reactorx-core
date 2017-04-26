package cz.filipproch.reactor.demo

import android.support.v7.widget.Toolbar
import cz.filipproch.reactor.base.translator.BaseReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.extras.ui.views.ToolbarReactorFragment

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class TestFragment : ToolbarReactorFragment<TestTranslator>() {
    override val translatorFactory: TranslatorFactory<TestTranslator>
        get() = TODO("not implemented")
    override val toolbar: Toolbar
        get() = TODO("not implemented")

    override fun getLayoutResId(): Int {
        TODO("not implemented")
    }
}

class TestTranslator : BaseReactorTranslator() {
    override fun onCreated() {

    }
}