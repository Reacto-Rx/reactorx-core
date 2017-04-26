package cz.filipproch.reactor.extras.ui.views.fragment

import android.os.Bundle
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.extras.ui.views.dialog.ExtendedReactorDialogFragment
import cz.filipproch.reactor.extras.ui.views.events.DialogResultEvent
import cz.filipproch.reactor.ui.ReactorFragment

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ExtendedReactorFragment<T : ReactorTranslator> :
        ReactorFragment<T>(),
        ExtendedReactorDialogFragment.DialogResultListener {

    override fun onDialogResult(requestCode: Int, resultCode: Int, extras: Bundle?) {
        dispatch(DialogResultEvent(requestCode, resultCode, extras))
    }

}