package cz.filipproch.reactor.demo.ui.main

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.demo.data.AwesomeNetworkModel
import cz.filipproch.reactor.ui.events.ViewCreatedEvent
import cz.filipproch.reactor.ui.events.whenViewCreatedFirstTime
import io.reactivex.ObservableTransformer

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class MainTranslator : ReactorTranslator() {

    override fun onCreated() {
        val fetchPostDetail = ObservableTransformer<ViewCreatedEvent, MainUiModel> {
            it.map { AwesomeNetworkModel.StuffRequest() }
                    .compose(AwesomeNetworkModel.fetchStuff)
                    .map {
                        when {
                            it.inProgress -> MainUiModel.LOADING
                            it.error != null -> MainUiModel.ERROR
                            it.stuffList != null -> MainUiModel.success("There is ${it.stuffList.size} stuff", "Thats a lot of stuff")
                            else -> MainUiModel.IDLE
                        }
                    }
                    .startWith(MainUiModel.IDLE)
        }

        translateToModel {
            whenViewCreatedFirstTime()
                    .compose(fetchPostDetail)
        }
    }

}