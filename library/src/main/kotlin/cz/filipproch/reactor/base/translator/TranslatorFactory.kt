package cz.filipproch.reactor.base.translator

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
interface TranslatorFactory<out T : ReactorTranslator> {

    /**
     * Called when new instance of the @{Translator} is needed
     * @return new @{T} instance
     */
    fun create(): T

}