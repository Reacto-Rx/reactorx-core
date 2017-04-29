package cz.filipproch.reactor.base.translator

import android.content.Context
import android.support.v4.content.Loader

/**
 * Simple synchronous loader to persist [Presenter] after device orientation and other configuration changes.

 * @author Filip Prochazka (@filipproch)
 */
class TranslatorLoader<T : ReactorTranslator>(context: Context, private val factory: TranslatorFactory<T>) :
        Loader<T>(context) {

    private var translatorInstance: T? = null

    override fun onStartLoading() {
        if (translatorInstance != null) {
            deliverResult(translatorInstance)
        } else {
            forceLoad()
        }
    }

    override fun onForceLoad() {
        translatorInstance = factory.create()
        translatorInstance?.onCreated()
        deliverResult(translatorInstance)
    }

    override fun onReset() {
        if (translatorInstance != null) {
            translatorInstance = null
        }
    }
}
