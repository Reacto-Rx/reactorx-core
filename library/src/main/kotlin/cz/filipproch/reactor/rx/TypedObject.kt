package cz.filipproch.reactor.rx

/**
 * Helper interface used by [TypeBehaviorSubject] and [cz.filipproch.reactor.base.view.ReactorUiModel]
 * for the persistence of <b>UI Models</b> during orientation changes and other events.
 */
interface TypedObject {

    fun getType(): Class<*>

}