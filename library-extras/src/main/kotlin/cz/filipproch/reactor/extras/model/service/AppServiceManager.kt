package cz.filipproch.reactor.extras.model.service

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction

/**
 * TODO: add description
 */
object AppServiceManager {

    private val servicesObservables = hashMapOf<String, Observable<out IAppService>>()

    @Suppress("UNCHECKED_CAST")
    fun <S : IAppService> observe(serviceClass: Class<S>): Observable<S> {
        val className = serviceClass.name
        if (AppServiceManager.servicesObservables.containsKey(className).not()) {
            AppServiceManager.servicesObservables[className] = AppServiceManager.createNewServiceObservable(serviceClass)
        }

        return AppServiceManager.servicesObservables[className] as Observable<S>
    }

    private fun <S : IAppService> createNewServiceObservable(serviceClass: Class<S>): Observable<S> {
        return Observable.create {
            val className = serviceClass.name

            val instance = serviceClass.newInstance()
            it.onNext(instance)

            it.setCancellable {
                instance.dispose()
                AppServiceManager.servicesObservables.remove(className)
            }
        }
    }

    fun <S : IAppService, T> combineToStream(serviceClass: Class<S>): ObservableTransformer<T, Pair<T, S>> {
        return ObservableTransformer {
            Observable.combineLatest(
                    it,
                    observe(serviceClass),
                    BiFunction { originalItem: T, service: S -> Pair(originalItem, service) }
            )
        }
    }

}