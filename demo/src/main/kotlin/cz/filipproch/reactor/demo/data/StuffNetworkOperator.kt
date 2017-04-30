package cz.filipproch.reactor.demo.data

import cz.filipproch.reactor.demo.model.Stuff
import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object StuffNetworkOperator {

    fun fetchStuff(): Observable<List<Stuff>> {
        return Observable.just(getRandomListOfStuff())
                .delay(10000, TimeUnit.MILLISECONDS)
    }

    private fun getRandomListOfStuff(): List<Stuff> {
        val random = Random()
        val list = mutableListOf<Stuff>()
        for (data in 1..random.nextInt(20)) {
            list.add(Stuff())
        }
        return list
    }

}