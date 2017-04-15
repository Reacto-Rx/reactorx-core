package cz.filipproch.reactor.demo.ui

import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import cz.filipproch.reactor.base.translator.SimpleTranslatorFactory
import cz.filipproch.reactor.base.translator.TranslatorFactory
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.demo.MainTranslator
import cz.filipproch.reactor.demo.MainUiModel
import cz.filipproch.reactor.demo.R
import cz.filipproch.reactor.ui.ReactorActivity
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : ReactorActivity<MainTranslator>() {

    override val translatorFactory: TranslatorFactory<MainTranslator>
        get() = SimpleTranslatorFactory(MainTranslator::class.java)

    override fun onConnectModelChannel(modelStream: Observable<out ReactorUiModel>) {
        receiveUpdatesOnUi(modelStream.ofType(MainUiModel::class.java)) {
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

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

}/*

class EventRegistrationImpossibleException : RuntimeException()*/

object MasterButtonClicked : ReactorUiEvent