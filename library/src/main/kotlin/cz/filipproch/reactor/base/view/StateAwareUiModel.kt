package cz.filipproch.reactor.base.view

/**
 * @author Filip Prochazka (@filipproch)
 */
abstract class StateAwareUiModel : ReactorUiModel {

    var cached: Boolean = false
        private set

    fun setCached() {
        cached = true
    }

}