package cz.filipproch.reactor.demo.data

import android.util.Log
import cz.filipproch.reactor.demo.StuffFetchResult
import cz.filipproch.reactor.demo.StuffRequest
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

}