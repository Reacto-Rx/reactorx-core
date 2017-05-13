package cz.filipproch.reactor.base.translator

import android.support.annotation.NonNull

/**
 * Simple implementation of the [TranslatorFactory].
 *
 * Internally uses the [Class.newInstance] method.
 */
class SimpleTranslatorFactory<T : IReactorTranslator>(val translatorClazz: Class<T>) : TranslatorFactory<T> {

    @NonNull
    override fun create(): T {
        try {
            return translatorClazz.newInstance()
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }

    }

}