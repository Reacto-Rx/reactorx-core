package cz.filipproch.reactor.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory

/**
 * Fragment that has no [android.view.View] and is used to retain [ReactorTranslator] instance
 *
 * @author Filip Prochazka (@filipproch)
 */
class ReactorTranslatorFragment<T : ReactorTranslator> : Fragment() {

    private lateinit var factory: TranslatorFactory<T>

    var translator: T? = null
        private set

    init {
        retainInstance = true
    }

    fun setTranslatorFactory(factory: TranslatorFactory<T>) {
        this.factory = factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        translator = factory.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        translator?.onDestroyed()
    }

    companion object {
        val TAG = "translator_fragment"
    }
}