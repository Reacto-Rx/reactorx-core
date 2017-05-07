package cz.filipproch.reactor.demo.ui.main

import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.demo.R
import cz.filipproch.reactor.extras.ui.views.activity.ExtendedReactorActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ExtendedReactorActivity<MainTranslator>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override val translatorFactory: TranslatorFactory<MainTranslator>
        get() = SimpleTranslatorFactory(MainTranslator::class.java)

    override fun onConnectModelStream(modelStream: Observable<out ReactorUiModel>) {
        super.onConnectModelStream(modelStream)
        modelStream.ofType(MainUiModel::class.java).consumeOnUi {
            vProgressBar.visibility = if (it.isLoading) View.VISIBLE else View.GONE

            when {
                it.success == true -> {
                    vTextView.text = it.postTitle
                    vTextView2.text = it.postContent
                }
                it.success == false -> {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onEmittersInit() {
        super.onEmittersInit()
        registerEmitter(vAction.clicks().map { MasterButtonClicked })
    }

}

object MasterButtonClicked : ReactorUiEvent