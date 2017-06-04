package cz.filipproch.reactor.ui.helper

import android.os.Bundle
import android.support.v4.app.Fragment
import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory

/**
 * Fragment that has no [android.view.View] and is used to retain [IReactorTranslator] instance
 */
class ReactorTranslatorFragment<T : cz.filipproch.reactor.base.translator.IReactorTranslator> : android.support.v4.app.Fragment() {

    private var factory: cz.filipproch.reactor.base.translator.TranslatorFactory<T>? = null

    var isInvalid: Boolean = false
        private set

    var isDestroyed: Boolean = true
        private set

    var translator: T? = null
        private set

    init {
        retainInstance = true
    }

    fun setTranslatorFactory(factory: cz.filipproch.reactor.base.translator.TranslatorFactory<T>) {
        this.factory = factory
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
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