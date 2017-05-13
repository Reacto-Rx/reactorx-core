package cz.filipproch.reactor.base.translator

/**
 * The main [IReactorTranslator] implementation. It uses [io.reactivex.subjects.Subject]
 * internally to keep the streams of data intact (without getting them disposed)
 */
@Deprecated("Renamed to ReactorTranslator", ReplaceWith(
        "ReactorTranslator"
))
abstract class BaseReactorTranslator : ReactorTranslator()