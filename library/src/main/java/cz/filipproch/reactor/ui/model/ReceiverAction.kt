package cz.filipproch.reactor.ui.model

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
interface ReceiverAction<in T> {
    fun invoke(model: T)
}