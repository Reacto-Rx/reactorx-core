package cz.filipproch.reactor.demo.data

import android.util.Log
import cz.filipproch.reactor.demo.model.Stuff
import io.reactivex.ObservableTransformer

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object AwesomeNetworkModel {

    val fetchStuff = ObservableTransformer<StuffRequest, StuffFetchResult> {
        it
                .doOnNext { Log.v("Z", "$it") }
                .flatMap {
                    StuffNetworkOperator.fetchStuff()
                            .doOnNext { Log.v("X", "$it") }
                            .map { StuffFetchResult.success(it) }
                            .onErrorReturn { StuffFetchResult.failure(it) }
                            .startWith(StuffFetchResult.inProgress())
                }
    }

    class StuffRequest

    class StuffFetchResult(val stuffList: List<Stuff>?, val error: Throwable?, val inProgress: Boolean) {
        companion object {
            fun success(stuffList: List<Stuff>): StuffFetchResult {
                return StuffFetchResult(stuffList, null, false)
            }

            fun failure(error: Throwable): StuffFetchResult {
                return StuffFetchResult(null, error, false)
            }

            fun inProgress(): StuffFetchResult {
                return StuffFetchResult(null, null, true)
            }
        }
    }

}