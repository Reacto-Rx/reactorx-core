package cz.filipproch.reactor.extras.ui.views.dialog

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.View
import cz.filipproch.reactor.base.translator.ReactorTranslator

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
@Deprecated("No implemented, does not work!!!")
abstract class ExtendedReactorDialogFragment<T : ReactorTranslator> : DialogFragment() {

    private val STATE_REQUEST_CODE = "request_code"

    private var requestCode: Int = -1

    private var resultListener: DialogResultListener? = null

    fun withRequestCode(requestCode: Int): ExtendedReactorDialogFragment<T> {
        this.requestCode = requestCode
        return this
    }

    fun dismissWithResult(resultCode: Int, extras: Bundle? = null) {
        resultListener?.onDialogResult(requestCode, resultCode, extras)
        dismiss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_REQUEST_CODE, requestCode)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            requestCode = savedInstanceState.getInt(STATE_REQUEST_CODE)
        }

        if (resultListener == null && targetFragment != null &&
                targetFragment is DialogResultListener) {
            resultListener = targetFragment as DialogResultListener
        }
    }

    override fun setTargetFragment(fragment: Fragment?, requestCode: Int) {
        super.setTargetFragment(fragment, requestCode)
        this.requestCode = requestCode
        if (fragment is DialogResultListener) {
            resultListener = fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DialogResultListener) {
            resultListener = context
        }
    }

    interface DialogResultListener {
        fun onDialogResult(requestCode: Int, resultCode: Int, extras: Bundle?)
    }

}