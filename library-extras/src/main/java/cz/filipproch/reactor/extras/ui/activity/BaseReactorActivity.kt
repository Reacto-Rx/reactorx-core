package cz.filipproch.reactor.extras.ui.activity

import android.content.Intent
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.extras.ui.actions.FinishActivityAction
import cz.filipproch.reactor.extras.ui.actions.FinishActivityWithResultAction
import cz.filipproch.reactor.extras.ui.actions.StartActivityAction
import cz.filipproch.reactor.extras.ui.actions.StartActivityForResultAction
import cz.filipproch.reactor.extras.ui.events.ActivityResultEvent
import cz.filipproch.reactor.ui.ReactorActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class BaseReactorActivity<T : ReactorTranslator> : ReactorActivity<T>() {

    val activityResultSubject: PublishSubject<ActivityResultEvent> = PublishSubject.create<ActivityResultEvent>()

    override fun onEmittersInit() {
        super.onEmittersInit()
        registerEmitter(activityResultSubject)
    }

    override fun onConnectActionChannel(actionStream: Observable<out ReactorUiAction>) {
        super.onConnectActionChannel(actionStream)
        receiveUpdatesOnUi(actionStream.ofType(FinishActivityAction::class.java)) {
            finish()
        }

        receiveUpdatesOnUi(actionStream.ofType(FinishActivityWithResultAction::class.java)) {
            setResult(it.resultCode)
            finish()
        }

        receiveUpdatesOnUi(actionStream.ofType(StartActivityAction::class.java)) {
            startActivity(Intent(this, it.activity))
        }

        receiveUpdatesOnUi(actionStream.ofType(StartActivityForResultAction::class.java)) {
            startActivityForResult(Intent(this, it.activity), it.requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        activityResultSubject.onNext(ActivityResultEvent(requestCode, resultCode, data))
    }

}