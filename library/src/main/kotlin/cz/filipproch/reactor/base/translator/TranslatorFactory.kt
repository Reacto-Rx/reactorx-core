package cz.filipproch.reactor.base.translator

/**
 * Factory interface for creating new instances of [IReactorTranslator]
 */
interface TranslatorFactory<out T : IReactorTranslator> {

    /**
     * Called when new instance of the @{Translator} is needed
     * @return new @{T} instance
     */
    fun create(): T

}