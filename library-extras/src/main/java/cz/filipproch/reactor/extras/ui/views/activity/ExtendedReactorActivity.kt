package cz.filipproch.reactor.extras.ui.views.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.extras.ui.iface.AndroidLayoutView
import cz.filipproch.reactor.extras.ui.views.actions.FinishActivityAction
import cz.filipproch.reactor.extras.ui.views.actions.FinishActivityWithResultAction
import cz.filipproch.reactor.extras.ui.views.actions.StartActivityAction
import cz.filipproch.reactor.extras.ui.views.actions.StartActivityForResultAction
import cz.filipproch.reactor.extras.ui.views.dialog.ExtendedReactorDialogFragment
import cz.filipproch.reactor.extras.ui.views.events.ActivityResultEvent
import cz.filipproch.reactor.extras.ui.views.events.DialogResultEvent
import cz.filipproch.reactor.extras.ui.views.model.ContentFragmentModel
import cz.filipproch.reactor.ui.ReactorActivity
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ExtendedReactorActivity<T : ReactorTranslator> :
        ReactorActivity<T>(),
        ExtendedReactorDialogFragment.DialogResultListener,
        AndroidLayoutView {

    private val CONTENT_FRAGMENT_TAG = "content_fragment"

    /**
     * [View] that will be replaced with [Fragment] when [replaceContentWithFragment] is called
     */
    open val contentView: View? = null

    override fun onCreateLayout() {
        setContentView(layoutResourceId)
    }

    override fun onConnectActionChannel(actionStream: Observable<out ReactorUiAction>) {
        super.onConnectActionChannel(actionStream)
        actionStream.ofType(FinishActivityAction::class.java).consumeOnUi {
            finish()
        }

        actionStream.ofType(FinishActivityWithResultAction::class.java).consumeOnUi {
            setResult(it.resultCode)
            finish()
        }

        actionStream.ofType(StartActivityAction::class.java).consumeOnUi {
            startActivity(Intent(this, it.activity))
        }

        actionStream.ofType(StartActivityForResultAction::class.java).consumeOnUi {
            startActivityForResult(Intent(this, it.activity), it.requestCode)
        }
    }

    override fun onConnectModelChannel(modelStream: Observable<out ReactorUiModel>) {
        super.onConnectModelChannel(modelStream)
        modelStream.ofType(ContentFragmentModel::class.java).consumeOnUi {
            replaceContentWithFragment(it.fragment)
        }
    }

    private fun replaceContentWithFragment(fragment: Fragment?, checkFragmentClass: Boolean = true) {
        val contentView = this.contentView
        if (contentView != null && fragment != null) {
            val existing = supportFragmentManager.findFragmentByTag(CONTENT_FRAGMENT_TAG)
            if (checkFragmentClass && existing != null && existing.javaClass == fragment.javaClass) {
                return
            }
            supportFragmentManager.beginTransaction()
                    .replace(contentView.id, fragment, CONTENT_FRAGMENT_TAG)
                    .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dispatch(ActivityResultEvent(requestCode, resultCode, data))
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, extras: Bundle?) {
        dispatch(DialogResultEvent(requestCode, resultCode, extras))
    }

}