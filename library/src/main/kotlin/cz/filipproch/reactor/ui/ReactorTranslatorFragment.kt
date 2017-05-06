package cz.filipproch.reactor.ui

import android.annotation.SuppressLint
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

    private var factory: TranslatorFactory<T>? = null
    internal var invalid: Boolean = false

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
        if (factory == null) {
            invalid = true
        }
        translator = factory?.create()
        translator?.onCreated()
    }

    @SuppressLint("CommitTransaction")
    override fun onDestroy() {
        super.onDestroy()
        translator?.onDestroyed()
    }

    companion object {
        val TAG = "translator_fragment"
    }
}