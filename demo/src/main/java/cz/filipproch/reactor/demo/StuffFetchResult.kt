package cz.filipproch.reactor.demo

import cz.filipproch.reactor.demo.model.Stuff

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
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