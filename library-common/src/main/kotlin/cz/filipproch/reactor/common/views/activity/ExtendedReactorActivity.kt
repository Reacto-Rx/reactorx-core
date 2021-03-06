package cz.filipproch.reactor.common.views.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.UiModel
import cz.filipproch.reactor.common.iface.AndroidLayoutView
import cz.filipproch.reactor.common.views.actions.FinishActivityAction
import cz.filipproch.reactor.common.views.actions.FinishActivityWithResultAction
import cz.filipproch.reactor.common.views.actions.StartActivityAction
import cz.filipproch.reactor.common.views.actions.StartActivityForResultAction
import cz.filipproch.reactor.common.views.dialog.ExtendedReactorDialogFragment
import cz.filipproch.reactor.common.views.events.ActivityResultEvent
import cz.filipproch.reactor.common.views.events.DialogResultEvent
import cz.filipproch.reactor.common.views.model.ContentFragmentModel
import org.reactorx.ui.ReactorActivity
import io.reactivex.Observable

/**
 * TODO: add description
 */
abstract class ExtendedReactorActivity<T : IReactorTranslator> :
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

    override fun onConnectActionStream(actionStream: Observable<out ReactorUiAction>) {
        super.onConnectActionStream(actionStream)

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

    override fun onConnectModelStream(modelStream: Observable<out UiModel>) {
        super.onConnectModelStream(modelStream)

        modelStream.ofType(ContentFragmentModel::class.java).consumeOnUi {
            replaceContentWithFragment(it.fragment)
        }
    }

    protected fun replaceContentWithFragment(fragment: Fragment?, checkFragmentClass: Boolean = true) {
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