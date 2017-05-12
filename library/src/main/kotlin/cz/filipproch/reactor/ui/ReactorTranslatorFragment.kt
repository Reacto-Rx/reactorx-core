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

    private var factory: TranslatorFactory<T>? = null

    var isInvalid: Boolean = false
        private set

    var isDestroyed: Boolean = true
        private set

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
            isInvalid = true
            return
        }
        translator = factory?.create()
        translator?.onInstanceCreated()
    }

    override fun onDestroy() {
        super.onDestroy()
        translator?.onBeforeInstanceDestroyed()
        isDestroyed = true
    }

    companion object {
        val TAG = "translator_fragment"
    }
}