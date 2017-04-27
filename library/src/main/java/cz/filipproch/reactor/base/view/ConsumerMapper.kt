package cz.filipproch.reactor.base.view

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
interface ConsumerMapper<in M: ReactorUiModel, out T> {
    fun mapModelToUi(model: M): T
}