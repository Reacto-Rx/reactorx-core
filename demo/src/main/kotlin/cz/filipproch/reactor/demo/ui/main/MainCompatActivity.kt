package cz.filipproch.reactor.demo.ui.main

import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.base.view.UiEvent
import cz.filipproch.reactor.base.view.UiModel
import cz.filipproch.reactor.common.views.activity.ExtendedReactorActivity
import cz.filipproch.reactor.demo.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainCompatActivity : ExtendedReactorActivity<MainTranslator>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override val translatorFactory: TranslatorFactory<MainTranslator>
        get() = SimpleTranslatorFactory(MainTranslator::class.java)

    override fun onConnectModelStream(modelStream: Observable<out UiModel>) {
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

object MasterButtonClicked : UiEvent