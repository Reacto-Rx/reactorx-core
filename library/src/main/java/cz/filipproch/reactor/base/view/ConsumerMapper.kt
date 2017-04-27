package cz.filipproch.reactor.base.view

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
interface ConsumerMapper<in M: ReactorUiModel, T> {
    fun mapModelToUi(model: M): T
}