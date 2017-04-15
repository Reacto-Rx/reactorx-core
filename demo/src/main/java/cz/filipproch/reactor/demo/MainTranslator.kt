package cz.filipproch.reactor.demo

import android.util.Log
import cz.filipproch.reactor.base.translator.BaseReactorTranslator
import cz.filipproch.reactor.demo.data.AwesomeNetworkModel
import cz.filipproch.reactor.ui.events.ViewCreatedEvent
import io.reactivex.ObservableTransformer

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class MainTranslator : BaseReactorTranslator() {

    override fun onCreated() {
        val fetchPostDetail = ObservableTransformer<ViewCreatedEvent, MainUiModel> {
            it.map { StuffRequest() }
                    .compose(AwesomeNetworkModel.fetchStuff)
                    .map {
                        when {
                            it.inProgress -> MainUiModel.loading()
                            it.error != null -> MainUiModel.error()
                            it.stuffList != null -> MainUiModel.success("There is ${it.stuffList.size} stuff", "Thats a lot of stuff")
                            else -> MainUiModel.idle()
                        }
                    }
                    .startWith(MainUiModel.idle())
        }

        translate {
            it.doOnNext { Log.v("TEst", "$it") }
                    .ofType(ViewCreatedEvent::class.java)
                    .compose(fetchPostDetail)
        }

        /*translate {
            it.ofType(MasterButtonClicked::class.java)
                    .compose()
        }*/
    }

}