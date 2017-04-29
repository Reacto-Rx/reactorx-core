package cz.filipproch.reactor.base.translator

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class SimpleTranslatorFactory<T : ReactorTranslator>(val translatorClazz: Class<T>) : TranslatorFactory<T> {
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